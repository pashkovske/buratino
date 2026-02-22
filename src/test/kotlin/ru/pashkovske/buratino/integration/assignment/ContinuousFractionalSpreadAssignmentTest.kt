package ru.pashkovske.buratino.integration.assignment

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.dao.postgre.ContinuousFractionalSpreadAssignmentDao
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

class ContinuousFractionalSpreadAssignmentTest(
    @Autowired mockMvc: MockMvc
): BasicAssignmentTest(
    mockMvc = mockMvc
) {

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var continuousFractionalSpreadAssignmentDao: ContinuousFractionalSpreadAssignmentDao
    @Autowired
    private lateinit var fractionalSpreadAssignmentDao: FractionalSpreadAssignmentDao
    @Autowired
    private lateinit var orderDao: OrderDao

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @BeforeEach
    fun setup() {
        orderDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
        continuousFractionalSpreadAssignmentDao.deleteAll()

    }

    @Test
    fun `create, skip refresh, cancel nested, continue and cancel buy`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.BUY
        val rate = 0.007

        // Create
        val createResult: MvcResult = performAndCheckCreate(
            path = "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = "{\"rate\": $rate}",
            params = null
        )
            .andExpect(jsonPath("$.nested.direction").value(direction.toString()))
            .andExpect(jsonPath("$.nested.rate").value(rate))
            .andExpect(jsonPath("$.nested.info.orderId").isString())
            .andReturn()

        verify(extOrderServiceAdapter).createOrder(any())

        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val nestedAssignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.nested.id"))
        val createdOrderId: String = JsonPath.parse(createResult.response.contentAsString).read("$.nested.info.orderId")

        val expectedPrice = Price(
            unit = 65,
            nano = 600_000_000,
            currency = Currency.RUB
        )
        val expectedOrderRequest = LimitOrderRequest(
            iid = iid,
            direction = direction,
            lots = 1,
            idempotencyToken = null,
            price = expectedPrice
        )
        expectOrderOnLimitedRequest(
            orderId = createdOrderId,
            expectedLimitedRequest = expectedOrderRequest
        )

        // Refresh
        performAndCheckRefresh(
            path = "/assignment/continuous/fractional-spread/{assignmentId}/refresh",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.nested.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.nested.info.orderId").isString())

        verify(extOrderServiceAdapter, never()).replaceOrder(any(), any())

        // Cancel nested
        performAndCheckCancel(
            path = "/assignment/fractional-spread/{assignmentId}",
            assignmentId = nestedAssignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.info.orderId").isString())

        verify(extOrderServiceAdapter).cancelOrder(createdOrderId)

        // Continue
        val continueResult: MvcResult = performAndCheckContinue(
            path = "/assignment/continuous/fractional-spread/{assignmentId}/continue",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.nested.direction").value(direction.getOpposite().toString()))
            .andExpect(jsonPath("$.nested.rate").value(rate))
            .andExpect(jsonPath("$.nested.info.orderId").isString())
            .andReturn()

        val continuedOrderId: String = JsonPath.parse(continueResult.response.contentAsString).read("$.nested.info.orderId")

        assertNotEquals(continuedOrderId, createdOrderId)

        // Cancel
        performAndCheckCancel(
            path = "/assignment/continuous/fractional-spread/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.nested.info.orderId").value(continuedOrderId))

        assertAllAssignmentsCancelled(
            path = "/assignment/continuous/fractional-spread/",
            expectedCount = 1
        )
        assertAllAssignmentsCancelled(
            path = "/assignment/fractional-spread/",
            expectedCount = 2
        )
        assertAllOrdersCancelled(2)
    }

    @Test
    fun `create with continue schedule and cancel sell`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val rate = 0.007

        // Create
        val createResult: MvcResult = performAndCheckCreate(
            path = "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = """
                {
                    "rate": $rate,
                    "continueSchedulingInterval": "PT10M"
                }
            """.trimIndent(),
            params = null
        ).andReturn()

        assertEquals(1, taskSchedulerFacade.getScheduled().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: ContinuousFractionalSpreadAssignment = continuousFractionalSpreadAssignmentDao.get(assignmentId)
        assertNotNull(assignment.getContinueSchedulingInfo())
        assertEquals(
            taskSchedulerFacade.getScheduled().first(),
            assignment.getContinueSchedulingInfo()!!.taskId
        )

        performAndCheckCancel(
            path = "/assignment/continuous/fractional-spread/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
        assertTrue(taskSchedulerFacade.getScheduled().isEmpty())
    }

    @Test
    fun `create with refresh schedule and cancel sell`()  {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val rate = 0.007

        // Create
        val createResult: MvcResult = performAndCheckCreate(
            path = "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = """
                {
                    "rate": $rate,
                    "refreshSchedulingInterval": "PT10M"
                }
            """.trimIndent(),
            params = null
        ).andReturn()
        assertEquals(1, taskSchedulerFacade.getScheduled().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: ContinuousFractionalSpreadAssignment = continuousFractionalSpreadAssignmentDao.get(assignmentId)
        assertNotNull(assignment.getRefreshSchedulingInfo())
        assertEquals(
            taskSchedulerFacade.getScheduled().first(),
            assignment.getRefreshSchedulingInfo()!!.taskId
        )

        performAndCheckCancel(
            path = "/assignment/continuous/fractional-spread/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
        assertTrue(taskSchedulerFacade.getScheduled().isEmpty())
    }
}
