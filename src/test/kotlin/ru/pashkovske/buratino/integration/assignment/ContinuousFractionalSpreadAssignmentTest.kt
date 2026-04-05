package ru.pashkovske.buratino.integration.assignment

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import ru.pashkovske.buratino.assignment.dao.core.postgre.ContinuousFractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.core.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.ContinueNotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

class ContinuousFractionalSpreadAssignmentTest(
    @Autowired mockMvc: MockMvc
) : BasicAssignmentTest(
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

    @Autowired
    private lateinit var refreshNotifierDao: RefreshNotifierDao

    @Autowired
    private lateinit var continueNotifierDao: ContinueNotifierDao

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun clean() {
        orderDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
        continuousFractionalSpreadAssignmentDao.deleteAll()
    }

    @Test
    fun `create, skip refresh, cancel child, continue and cancel buy`() {
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
            .andExpect(jsonPath("$.child.direction").value(direction.toString()))
            .andExpect(jsonPath("$.child.rate").value(rate))
            .andExpect(jsonPath("$.child.info.orderId").isString())
            .andReturn()

        verify(extOrderServiceAdapter).createOrder(any())

        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val childAssignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.child.id"))
        val createdOrderId: String = JsonPath.parse(createResult.response.contentAsString).read("$.child.info.orderId")

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
            .andExpect(jsonPath("$.child.state").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.child.info.orderId").isString())

        verify(extOrderServiceAdapter, never()).replaceOrder(any(), any())

        // Cancel child
        performAndCheckCancel(
            path = "/assignment/fractional-spread/{assignmentId}",
            assignmentId = childAssignmentId,
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
            .andExpect(jsonPath("$.child.direction").value(direction.getOpposite().toString()))
            .andExpect(jsonPath("$.child.rate").value(rate))
            .andExpect(jsonPath("$.child.info.orderId").isString())
            .andReturn()

        val continuedOrderId: String = JsonPath.parse(continueResult.response.contentAsString).read("$.child.info.orderId")

        assertNotEquals(continuedOrderId, createdOrderId)

        // Cancel
        performAndCheckCancel(
            path = "/assignment/continuous/fractional-spread/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.child.info.orderId").value(continuedOrderId))

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
    fun `create with continue notifier and cancel sell`() {
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
                    "continueNotifyPeriod": "PT10M"
                }
            """.trimIndent(),
            params = null
        ).andReturn()

        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: ContinuousFractionalSpreadAssignment = continuousFractionalSpreadAssignmentDao.get(assignmentId)
        val continueNotifierId: UUID? = assignment.getContinueNotifierId()
        assertNotNull(continueNotifierId)
        val continueNotifier: AssignmentNotifier = continueNotifierDao.get(continueNotifierId!!)
        assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            continueNotifier.taskId
        )

        performAndCheckCancel(
            path = "/assignment/continuous/fractional-spread/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
        assertTrue(taskScheduler.getPeriodicScheduledTasks().isEmpty())
    }

    @Test
    fun `create with refresh notifier and cancel sell`() {
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
                    "refreshNotifyPeriod": "PT10M"
                }
            """.trimIndent(),
            params = null
        ).andReturn()
        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: ContinuousFractionalSpreadAssignment = continuousFractionalSpreadAssignmentDao.get(assignmentId)
        val refreshNotifierId: UUID? = assignment.getRefreshNotifierId()
        assertNotNull(refreshNotifierId)
        val refreshNotifier: AssignmentNotifier = refreshNotifierDao.get(refreshNotifierId!!)
        assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            refreshNotifier.taskId
        )

        performAndCheckCancel(
            path = "/assignment/continuous/fractional-spread/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
        assertTrue(taskScheduler.getPeriodicScheduledTasks().isEmpty())
    }
}
