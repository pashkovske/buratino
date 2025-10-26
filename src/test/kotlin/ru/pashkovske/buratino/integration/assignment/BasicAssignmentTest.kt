package ru.pashkovske.buratino.integration.assignment

import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.`is`
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

abstract class BasicAssignmentTest {
    protected fun assertAllCancelled(
        path: String,
        mockMvc: MockMvc
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(path)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].status",
                everyItem(`is`("COMPLETED"))))
    }
}
