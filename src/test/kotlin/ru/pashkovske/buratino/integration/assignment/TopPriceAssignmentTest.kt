package ru.pashkovske.buratino.integration.assignment

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.assignment.limit.top.price.controller.TopPriceAssignmentController

@WebMvcTest(TopPriceAssignmentController::class)
@Import(IntegrationStubsConfiguration::class)
class TopPriceAssignmentTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper

    private lateinit var iid: InstrumentId

    @BeforeEach
    fun setup() {
        iid = bootstrapper.prepareKZOSData()
    }

    @Test
    fun `should create top price assignment for sell direction with oneStepOver`() {
        val direction = "sell"
        val oneStepOver = true

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    direction
                )
                .param("oneStepOver", oneStepOver.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.iid.id").value(iid.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value("SELL"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.lastUpdate").exists())
    }
}
