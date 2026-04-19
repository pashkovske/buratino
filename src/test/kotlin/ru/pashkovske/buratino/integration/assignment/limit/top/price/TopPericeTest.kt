package ru.pashkovske.buratino.integration.assignment.limit.top.price

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.util.MultiValueMap
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.assignment.BasicAssignmentTest
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.Locale.getDefault
import java.util.UUID

abstract class TopPericeTest(
    @Autowired mockMvc: MockMvc
) : BasicAssignmentTest(
    mockMvc = mockMvc
) {

    protected fun create(
        iid: InstrumentId,
        direction: OrderDirection,
        oneStepOver: Boolean = false,
        refreshPeriod: String? = null
    ): MvcResult {
        val content: ObjectNode = JsonNodeFactory.instance.objectNode()
        if (refreshPeriod != null) {
            content.put("refreshNotifyPeriod", refreshPeriod)
        }
        val params: Map<String, String> = mapOf("oneStepOver" to oneStepOver.toString())

        var result: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    direction.toString().lowercase(getDefault())
                )
                .header("X-API-KEY", "test-api-key")
                .content(objectMapper.writeValueAsString(content))
                .params(MultiValueMap.fromSingleValue(params))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.state").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.direction").value(direction.toString()))
            .andExpect(jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(jsonPath("$.info.orderId").isString())

        if (refreshPeriod != null) {
            result = result
                .andExpect(jsonPath("$.refreshNotifier.properties.period").value(refreshPeriod))
        }

        return result.andReturn()
    }

    protected fun refresh(assignmentId: UUID): MvcResult {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .patch(
                    "/assignment/top-price/{assignmentId}/refresh",
                    assignmentId
                )
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.state").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.info.orderId").isString())
            .andReturn()
    }

    protected fun cancel(assignmentId: UUID): MvcResult {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .delete(
                    "/assignment/top-price/{assignmentId}",
                    assignmentId
                )
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.state").value("COMPLETED"))
            .andExpect(jsonPath("$.info.orderId").isString())
            .andReturn()
    }
}
