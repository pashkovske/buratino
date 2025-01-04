package ru.pashkovske.buratino.tinkoff.integration.assignment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.pashkovske.buratino.tinkoff.configuration.AppTestConfiguration;
import ru.pashkovske.buratino.tinkoff.service.assignment.controller.AssignmentController;

@Import(AppTestConfiguration.class)
@WebMvcTest(AssignmentController.class)
public class BestPriceFastAssignmentTest {
    @Autowired
    MockMvc mvc;

    @Test
    void newBuyAssignment() throws Exception {
        String ticker = "DATA";

        mvc.perform(post("/instrument/" + ticker + "/assignment/follow-best-price/buy")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentOrder.id").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.instrumentTicker").value(ticker))
                .andExpect(jsonPath("$.currentOrder.lotPrice.units").value(121))
                .andExpect(jsonPath("$.currentOrder.lotPrice.nanos").value(80000000))
                .andExpect(jsonPath("$.currentOrder.lotsQuantity").value(1))
                .andExpect(jsonPath("$.currentOrder.direction").value("ORDER_DIRECTION_BUY"));
    }
}
