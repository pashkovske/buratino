package ru.pashkovske.buratino.integration.assignment.limit.top.price

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.pashkovske.buratino.assignment.dao.core.postgre.TopPriceAssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.assignment.BasicAssignmentTest
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

class TopPriceAssignmentCreateTest(
    @Autowired mockMvc: MockMvc
): TopPericeTest(
    mockMvc = mockMvc
) {

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var topPriceAssignmentDao: TopPriceAssignmentDao
    @Autowired
    private lateinit var orderDao: OrderDao
    @Autowired
    private lateinit var refreshNotifierDao: RefreshNotifierDao

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        topPriceAssignmentDao.deleteAll()
    }

    @Test
    fun `create sell share`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val oneStepOver = true

        val result: MvcResult = performAndCheckCreate(
            path = "/assignment/top-price/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = "{}",
            params = mapOf("oneStepOver" to oneStepOver.toString())
        )
            .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value(direction.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.oneStepOver").value(oneStepOver))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info.orderId").isString())
            .andReturn()

        Mockito.verify(extOrderServiceAdapter).createOrder(any())

        val assignmentId: UUID = UUID.fromString(JsonPath.parse(result.response.contentAsString).read("$.id"))
        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

        val expectedPrice = Price(
            unit = 66,
            nano = 100_000_000,
            currency = Currency.RUB
        )
        val expectedOrderRequest = LimitOrderRequest(
            iid = iid,
            direction = direction,
            lots = 1,
            idempotencyToken = null,
            price = expectedPrice
        )
        expectOrderOnLimitedRequest(
            orderId = orderId,
            expectedLimitedRequest = expectedOrderRequest
        )
    }

    @Test
    fun `create with refresh notifier`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val oneStepOver = true

        val createResult: MvcResult = performAndCheckCreate(
            path = "/assignment/top-price/{instrumentId}/start/{direction}",
            iid = iid,
            direction = direction,
            content = "{\"refreshNotifyPeriod\": \"PT10M\"}",
            params = mapOf("oneStepOver" to oneStepOver.toString())
        ).andReturn()

        Assertions.assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: TopPriceAssignment = topPriceAssignmentDao.get(assignmentId)
        val refreshNotifierId: UUID? = assignment.getRefreshNotifierId()
        Assertions.assertNotNull(refreshNotifierId)
        val refreshNotifier: AssignmentNotifier = refreshNotifierDao.get(refreshNotifierId!!)
        Assertions.assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            refreshNotifier.taskId
        )
    }
}