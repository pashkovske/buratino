package ru.pashkovske.buratino.integration.assignment

import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest

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
}
