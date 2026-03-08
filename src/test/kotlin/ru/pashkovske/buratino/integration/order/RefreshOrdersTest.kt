package ru.pashkovske.buratino.integration.order

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(IntegrationStubsConfiguration::class)
@SpringBootTest
class RefreshOrdersTest(
    @param:Autowired private val mockMvc: MockMvc
) {

    @Autowired
    private lateinit var orderService: OrderService
    @Autowired
    private lateinit var orderDao: OrderDao
    @Autowired
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @AfterEach
    fun setUp() {
        orderDao.deleteAll()
    }

    @Test
    fun `refreshOrders should update currentInfo of active orders`() {
        orderDao.deleteAll()

        val orderRequest1 = LimitOrderRequest(
            iid = InstrumentId("test-instrument-id-1"),
            direction = OrderDirection.BUY,
            lots = 10,
            idempotencyToken = null,
            price = Price(unit = 100, nano = 0, currency = Currency.RUB)
        )
        val order1: Order = extOrderServiceAdapter.createOrder(orderRequest1)
        orderDao.create(order1)

        val orderRequest2 = LimitOrderRequest(
            iid = InstrumentId("test-instrument-id-2"),
            direction = OrderDirection.SELL,
            lots = 5,
            idempotencyToken = null,
            price = Price(unit = 200, nano = 0, currency = Currency.RUB)
        )
        val order2: Order = extOrderServiceAdapter.createOrder(orderRequest2)
        orderDao.create(order2)
        extOrderServiceAdapter.cancelOrder(order2.id)

        val orderRequest3 = LimitOrderRequest(
            iid = InstrumentId("test-instrument-id-3"),
            direction = OrderDirection.BUY,
            lots = 3,
            idempotencyToken = null,
            price = Price(unit = 300, nano = 0, currency = Currency.RUB)
        )
        val order3: Order = extOrderServiceAdapter.createOrder(orderRequest3)
        orderDao.create(order3)
        extOrderServiceAdapter.cancelOrder(order3.id)
        order3.currentInfo = extOrderServiceAdapter.getOrderActualInfo(order3.id)
        orderDao.update(order3)

        val responseBeforeRefresh: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()

        val ordersBeforeRefresh: JsonNode = objectMapper.readTree(responseBeforeRefresh.response.contentAsString)
        assertTrue(ordersBeforeRefresh.isArray)
        assertEquals(3, ordersBeforeRefresh.size())

        val order1BeforeRefresh: JsonNode? = ordersBeforeRefresh.firstOrNull { jsonNode: JsonNode ->
            jsonNode.get("id").asText() == order1.id
        }
        assertNotNull(order1BeforeRefresh)
        assertEquals("ACTIVE", order1BeforeRefresh!!.get("currentInfo").get("state").asText())

        val order2BeforeRefresh: JsonNode? = ordersBeforeRefresh.firstOrNull { jsonNode: JsonNode ->
            jsonNode.get("id").asText() == order2.id
        }
        assertNotNull(order2BeforeRefresh)
        assertEquals("ACTIVE", order2BeforeRefresh!!.get("currentInfo").get("state").asText())

        val order3BeforeRefresh: JsonNode? = ordersBeforeRefresh.firstOrNull { jsonNode: JsonNode ->
            jsonNode.get("id").asText() == order3.id
        }
        assertNotNull(order3BeforeRefresh)
        assertEquals("COMPLETED", order3BeforeRefresh!!.get("currentInfo").get("state").asText())

        orderService.refreshOrders()

        val responseAfterRefresh: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/order/")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()

        val ordersAfterRefresh: JsonNode = objectMapper.readTree(responseAfterRefresh.response.contentAsString)
        assertTrue(ordersAfterRefresh.isArray)
        assertEquals(3, ordersAfterRefresh.size())

        val order1AfterRefresh: JsonNode? = ordersAfterRefresh.firstOrNull { jsonNode: JsonNode ->
            jsonNode.get("id").asText() == order1.id
        }
        assertNotNull(order1AfterRefresh)
        assertEquals("ACTIVE", order1AfterRefresh!!.get("currentInfo").get("state").asText())

        val order2AfterRefresh: JsonNode? = ordersAfterRefresh.firstOrNull { jsonNode: JsonNode ->
            jsonNode.get("id").asText() == order2.id
        }
        assertNotNull(order2AfterRefresh)
        assertEquals("COMPLETED", order2AfterRefresh!!.get("currentInfo").get("state").asText())

        val order3AfterRefresh: JsonNode? = ordersAfterRefresh.firstOrNull { jsonNode: JsonNode ->
            jsonNode.get("id").asText() == order3.id
        }
        assertNotNull(order3AfterRefresh)
        assertEquals("COMPLETED", order3AfterRefresh!!.get("currentInfo").get("state").asText())
    }
}
