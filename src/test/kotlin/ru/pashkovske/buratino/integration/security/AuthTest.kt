package ru.pashkovske.buratino.integration.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AuthTest(
    @param:Autowired private val mockMvc: MockMvc
) {

    @Test
    fun noAuth() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun badAuth() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
                .header("X-API-KEY", "bad-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun goodAuth() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
