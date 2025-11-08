package ru.pashkovske.buratino.integration.assignment

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.repo.TopPriceAssignmentRepoInMemory
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.money.model.Currency
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import java.util.UUID

@WebMvcTest
@Import(IntegrationStubsConfiguration::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TopPriceAssignmentTest(
    @Autowired mockMvc: MockMvc
): BasicAssignmentTest(
    mockMvc = mockMvc
) {
    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var assignmentTaskScheduler: AssignmentTaskScheduler
    @Autowired
    private lateinit var topPriceAssignmentRepo: TopPriceAssignmentRepoInMemory

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @Test
    fun `should create, skip refresh and cancel sell`() {
        // Create
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val oneStepOver = true

        val result: MvcResult = performAndCheckCreate(
            path = "/assignment/top-price/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = "{}",
            params = mapOf("oneStepOver" to oneStepOver.toString())
        )
            .andExpect(jsonPath("$.direction").value(direction.toString()))
            .andExpect(jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())
            .andReturn()

        verify(extOrderServiceAdapter).createOrder(any())

        val assignmentId: UUID = UUID.fromString(JsonPath.parse(result.response.contentAsString).read("$.id"))
        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

        val expectedPrice = MoneyPrice(
            units = 66,
            nano = 100_000_000,
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
            orderId = orderId,
            expectedLimitedRequest = expectedOrderRequest
        )

        // Refresh
        performAndCheckRefresh(
            path = "/assignment/top-price/{assignmentId}/refresh",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter, never()).replaceOrder(any(), any())

        // Cancel
        performAndCheckCancel(
            path = "/assignment/top-price/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter).cancelOrder(orderId)

        assertAllAssignmentsCancelled(
            path = "/assignment/top-price/",
            expectedCount = 1
        )
        assertAllOrdersCancelled(1)
    }

    @Test
    fun `create with refresh schedule and cancel sell`()  {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val oneStepOver = true

        // Create
        val createResult: MvcResult = performAndCheckCreate(
            path = "/assignment/top-price/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = "{\"refreshSchedulingInterval\": \"PT10M\"}",
            params = mapOf("oneStepOver" to oneStepOver.toString())
        ).andReturn()

        assertEquals(1, assignmentTaskScheduler.getScheduled().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: TopPriceAssignment = topPriceAssignmentRepo.get(assignmentId)
        assertNotNull(assignment.getRefreshSchedulingInfo())
        assertEquals(
            assignmentTaskScheduler.getScheduled().first(),
            assignment.getRefreshSchedulingInfo()!!.taskId
        )

        performAndCheckCancel(
            path = "/assignment/top-price/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
        assertTrue(assignmentTaskScheduler.getScheduled().isEmpty())
    }
}
