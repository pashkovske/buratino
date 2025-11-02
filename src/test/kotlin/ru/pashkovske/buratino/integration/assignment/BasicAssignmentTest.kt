package ru.pashkovske.buratino.integration.assignment

import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

abstract class BasicAssignmentTest {
    protected fun assertAllAssignmentsCancelled(
        path: String,
        mockMvc: MockMvc,
        expectedCount: Int
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(path)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize<Any>(expectedCount)))
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$[*].status",
                    everyItem(`is`("COMPLETED"))
                )
            )
    }

    protected fun assertAllOrdersCancelled(
        mockMvc: MockMvc,
        expectedCount: Int
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize<Any>(expectedCount)))
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$[*].currentInfo.state",
                    everyItem(`is`("COMPLETED"))
                )
            )
    }
}
