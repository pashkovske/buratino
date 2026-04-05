package ru.pashkovske.buratino.integration.assignment

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
import org.springframework.util.MultiValueMap
import ru.pashkovske.buratino.assignment.dao.notify.NotifierDao
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import java.util.Locale.getDefault
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
    private lateinit var assignmentNotifierDaos: List<NotifierDao>

    @AfterEach
    fun tearDown() {
        assignmentNotifierDaos.forEach { it.deleteAll() }
        taskScheduler.shutdown()
    }

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

    protected fun expectOrderOnLimitedRequest(
        orderId: String,
        expectedLimitedRequest: LimitOrderRequest
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
            .andExpect(jsonPath("$.iid.id").value(expectedLimitedRequest.iid.id))
            .andExpect(jsonPath("$.request.direction").value(expectedLimitedRequest.direction.toString()))
            .andExpect(jsonPath("$.request.lots").value(expectedLimitedRequest.lots))
            .andExpect(jsonPath("$.currentInfo.remainingLots").value(expectedLimitedRequest.lots))
            .andExpect(jsonPath("$.request.price.unit").value(expectedLimitedRequest.price.unit))
            .andExpect(jsonPath("$.request.price.nano").value(expectedLimitedRequest.price.nano))
            .andExpect(jsonPath("$.request.price.currency").value(expectedLimitedRequest.price.currency.toString()))
    }

    protected fun performAndCheckCreate(
        path: String,
        iid: InstrumentId,
        direction: OrderDirection,
        content: String?,
        params: Map<String, String>?
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    path,
                    iid.id,
                    direction.toString().lowercase(getDefault())
                )
                .header("X-API-KEY", "test-api-key")
                .content(content ?: "")
                .params(MultiValueMap.fromSingleValue(params ?: emptyMap()))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.state").value("IN_PROGRESS"))
    }

    protected fun performAndCheckRefresh(
        path: String,
        assignmentId: UUID,
        iid: InstrumentId
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .patch(
                    path,
                    assignmentId
                )
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.state").value("IN_PROGRESS"))
    }

    protected fun performAndCheckCancel(
        path: String,
        assignmentId: UUID,
        iid: InstrumentId
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
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.state").value("COMPLETED"))
    }

    @Suppress("SameParameterValue")
    protected fun performAndCheckContinue(
        path: String,
        assignmentId: UUID,
        iid: InstrumentId
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .patch(
                    path,
                    assignmentId
                )
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.state").value("IN_PROGRESS"))
    }
}
