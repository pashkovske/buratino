package ru.pashkovske.buratino.integration.assignment

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import ru.pashkovske.buratino.assignment.dao.notify.NotifierDao
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import java.util.UUID

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(IntegrationStubsConfiguration::class)
@SpringBootTest
abstract class BasicAssignmentTest(
    protected val mockMvc: MockMvc
) {

    @Autowired
    protected lateinit var taskScheduler: TaskScheduler

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var assignmentNotifierDaos: List<NotifierDao>

    @AfterEach
    fun tearDown() {
        assignmentNotifierDaos.forEach { it.deleteAll() }
        taskScheduler.shutdown()
    }

    @Suppress("SameParameterValue")
    protected fun assertAllAssignmentsCancelled(
        path: String,
        expectedCount: Int
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(path)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$", hasSize<Any>(expectedCount)))
            .andExpect(
                jsonPath(
                    "$[*].state",
                    everyItem(`is`("COMPLETED"))
                )
            )
    }

    @Suppress("SameParameterValue")
    protected fun assertAllOrdersCancelled(expectedCount: Int) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$", hasSize<Any>(expectedCount)))
            .andExpect(
                jsonPath(
                    "$[*].currentInfo.state",
                    everyItem(`is`("COMPLETED"))
                )
            )
    }

    protected fun expectOrder(
        orderId: String,
        expected: LimitOrderRequest
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(
                    "/order/{id}",
                    orderId
                )
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(expected.iid.id))
            .andExpect(jsonPath("$.request.direction").value(expected.direction.toString()))
            .andExpect(jsonPath("$.request.lots").value(expected.lots))
            .andExpect(jsonPath("$.currentInfo.remainingLots").value(expected.lots))
            .andExpect(jsonPath("$.request.price.unit").value(expected.price.unit))
            .andExpect(jsonPath("$.request.price.nano").value(expected.price.nano))
            .andExpect(jsonPath("$.request.price.currency").value(expected.price.currency.toString()))
    }

    @Suppress("SameParameterValue")
    protected fun performAndCheckCancel(
        path: String,
        assignmentId: UUID
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .delete(
                    path,
                    assignmentId
                )
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.state").value("COMPLETED"))
    }
}
