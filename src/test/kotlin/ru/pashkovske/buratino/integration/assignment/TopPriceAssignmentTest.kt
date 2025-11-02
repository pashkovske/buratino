package ru.pashkovske.buratino.integration.assignment

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.money.model.Currency
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import java.util.Locale.getDefault

@WebMvcTest
@Import(IntegrationStubsConfiguration::class)
class TopPriceAssignmentTest(
    @Autowired mockMvc: MockMvc
): BasicAssignmentTest(
    mockMvc = mockMvc
) {
    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @Test
    fun `should create, skip refresh and cancel sell`() {
        // Create
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val oneStepOver = true

        val result: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    direction.toString().lowercase(getDefault())
                )
                .param("oneStepOver", oneStepOver.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.iid.id").value(iid.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value(direction.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.lastUpdate").exists())
            .andReturn()

        verify(extOrderServiceAdapter).createOrder(any())

        val assignmentId: String = JsonPath.parse(result.response.contentAsString).read("$.id")
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
        mockMvc.perform(
            MockMvcRequestBuilders
                .patch(
                    "/assignment/top-price/{assignmentId}/refresh",
                    assignmentId
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.iid.id").value(iid.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value(direction.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(assignmentId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").value(orderId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter, never()).replaceOrder(any(), any())

        // Cancel
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete(
                    "/assignment/top-price/{assignmentId}",
                    assignmentId
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.iid.id").value(iid.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value(direction.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(assignmentId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("COMPLETED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").value(orderId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter).cancelOrder(orderId)

        assertAllAssignmentsCancelled(
            path = "/assignment/top-price/",
            expectedCount = 1
        )
        assertAllOrdersCancelled(1)
    }
}
