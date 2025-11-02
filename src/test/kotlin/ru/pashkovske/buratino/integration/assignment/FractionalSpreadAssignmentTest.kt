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
import ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.FractionalSpreadAssignmentController
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter

@WebMvcTest(FractionalSpreadAssignmentController::class)
@Import(IntegrationStubsConfiguration::class)
class FractionalSpreadAssignmentTest: BasicAssignmentTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @Test
    fun `create, skip refresh and cancel buy`() {
        // Create
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = "buy"
        val rate = 0.007

        val result: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/fractional-spread/{instrumentId}/start/{direction}",
                    iid.id,
                    direction
                )
                .content("{\"rate\": $rate}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.iid.id").value(iid.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value("BUY"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(rate))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.lastUpdate").exists())
            .andReturn()

        verify(extOrderServiceAdapter).createOrder(any())

        val assignmentId: String = JsonPath.parse(result.response.contentAsString).read("$.id")
        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

        // Refresh
        mockMvc.perform(
            MockMvcRequestBuilders
                .patch(
                    "/assignment/fractional-spread/{assignmentId}/refresh",
                    assignmentId
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.iid.id").value(iid.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value("BUY"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(rate))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(assignmentId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter, never()).replaceOrder(any(), any())

        // Cancel
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete(
                    "/assignment/fractional-spread/{assignmentId}",
                    assignmentId
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.iid.id").value(iid.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value("BUY"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(rate))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(assignmentId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("COMPLETED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.lastUpdate").exists())

        verify(extOrderServiceAdapter).cancelOrder(orderId)

        assertAllAssignmentsCancelled(
            path = "/assignment/fractional-spread/",
            mockMvc = mockMvc,
            expectedCount = 1
        )
        assertAllOrdersCancelled(
            mockMvc = mockMvc,
            expectedCount = 1
        )
    }
}
