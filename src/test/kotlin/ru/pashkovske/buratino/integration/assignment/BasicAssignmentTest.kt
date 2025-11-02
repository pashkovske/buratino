package ru.pashkovske.buratino.integration.assignment

import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.util.MultiValueMap
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import java.util.Locale.getDefault
import java.util.UUID

abstract class BasicAssignmentTest(
    protected val mockMvc: MockMvc
) {
    protected fun assertAllAssignmentsCancelled(
        path: String,
        expectedCount: Int
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(path)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$", hasSize<Any>(expectedCount)))
            .andExpect(
                jsonPath(
                    "$[*].status",
                    everyItem(`is`("COMPLETED"))
                )
            )
    }

    protected fun assertAllOrdersCancelled(expectedCount: Int) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
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
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(expectedLimitedRequest.iid.id))
            .andExpect(jsonPath("$.request.direction").value(expectedLimitedRequest.direction.toString()))
            .andExpect(jsonPath("$.request.lots").value(expectedLimitedRequest.lots))
            .andExpect(jsonPath("$.currentInfo.remainingLots").value(expectedLimitedRequest.lots))
            .andExpect(jsonPath("$.request.price.units").value(expectedLimitedRequest.price.units))
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
                .content(content ?: "")
                .params(MultiValueMap.fromSingleValue(params ?: emptyMap()))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.direction").value(direction.toString()))
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())
    }

    protected fun performAndCheckRefresh(
        path: String,
        assignmentId: UUID,
        iid: InstrumentId,
        direction: OrderDirection
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .patch(
                    path,
                    assignmentId
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.direction").value(direction.toString()))
            .andExpect(jsonPath("$.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.info.orderId").isString())
            .andExpect(jsonPath("$.info.lastUpdate").exists())
    }

    protected fun performAndCheckCancel(
        path: String,
        assignmentId: UUID,
        iid: InstrumentId,
        direction: OrderDirection,
        orderId: String
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .delete(
                    path,
                    assignmentId
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.iid.id").value(iid.id))
            .andExpect(jsonPath("$.direction").value(direction.toString()))
            .andExpect(jsonPath("$.id").value(assignmentId.toString()))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.info.orderId").value(orderId))
            .andExpect(jsonPath("$.info.lastUpdate").exists())
    }
}
