package ru.pashkovske.buratino.integration.assignment.limit.top.price

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.assignment.BasicAssignmentTest
import ru.pashkovske.buratino.order.model.OrderDirection

class TopPericeTest(
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

        var result: ResultActions = performAndCheckCreate(
            path = "/assignment/top-price/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = content.toPrettyString(),
            params = params
        )
            .andExpect(jsonPath("$.direction").value(direction.toString()))
            .andExpect(jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(jsonPath("$.info.orderId").isString())

        if (refreshPeriod != null) {
            result = result
                .andExpect(jsonPath("$.refreshNotifier.properties.period").value(refreshPeriod))
        }

        return result.andReturn()
    }
}
