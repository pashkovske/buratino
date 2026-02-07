package ru.pashkovske.buratino.integration.assignment

import com.jayway.jsonpath.JsonPath
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
import ru.pashkovske.buratino.assignment.limit.spread.fraction.repo.postgre.FractionalSpreadAssignmentRepo
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.repo.OrderRepo
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

class FractionalSpreadAssignmentTest(
    @Autowired mockMvc: MockMvc
): BasicAssignmentTest(
    mockMvc = mockMvc
) {

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var fractionalAssignmentRepo: FractionalSpreadAssignmentRepo
    @Autowired
    private lateinit var orderRepo: OrderRepo

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @BeforeEach
    fun setup() {
        orderRepo.deleteAll()
        fractionalAssignmentRepo.deleteAll()
    }

    @Test
    fun `create, skip refresh and cancel buy`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.BUY
        val rate = 0.007

        // Create
        val result: MvcResult = performAndCheckCreate(
            path = "/assignment/fractional-spread/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = "{\"rate\": $rate}",
            params = null
        )
            .andExpect(jsonPath("$.direction").value(direction.toString()))
            .andExpect(jsonPath("$.rate").value(rate))
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())
            .andReturn()

        verify(extOrderServiceAdapter).createOrder(any())

        val assignmentId: UUID = UUID.fromString(JsonPath.parse(result.response.contentAsString).read("$.id"))
        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

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
            orderId = orderId,
            expectedLimitedRequest = expectedOrderRequest
        )

        // Refresh
        performAndCheckRefresh(
            path = "/assignment/fractional-spread/{assignmentId}/refresh",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter, never()).replaceOrder(any(), any())

        // Cancel
        performAndCheckCancel(
            path = "/assignment/fractional-spread/{assignmentId}",
            assignmentId = assignmentId,
            iid = iid
        )
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter).cancelOrder(orderId)

        assertAllAssignmentsCancelled(
            path = "/assignment/fractional-spread/",
            expectedCount = 1
        )
        assertAllOrdersCancelled(1)
    }
}
