package ru.pashkovske.buratino.integration.assignment.limit.top.price

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import ru.pashkovske.buratino.assignment.dao.core.postgre.TopPriceAssignmentDao
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID
import kotlin.test.assertTrue

class TopPriceAssignmentCancelTest(
    @Autowired mockMvc: MockMvc
) : TopPericeTest(
    mockMvc = mockMvc
) {

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var topPriceAssignmentDao: TopPriceAssignmentDao
    @Autowired
    private lateinit var orderDao: OrderDao

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        topPriceAssignmentDao.deleteAll()
    }

    @Test
    fun `cancel sell share`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL

        val result: MvcResult = create(
            iid = iid,
            direction = direction
        )
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(result.response.contentAsString).read("$.id"))
        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

        cancel(assignmentId)

        verify(extOrderServiceAdapter).cancelOrder(orderId)

        assertAllAssignmentsCancelled(
            path = "/assignment/top-price/",
            expectedCount = 1
        )
        assertAllOrdersCancelled(1)
    }

    @Test
    fun `cancel sell share with refresh notifier`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL

        val result: MvcResult = create(
            iid = iid,
            direction = direction,
            refreshPeriod = "PT10M"
        )
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(result.response.contentAsString).read("$.id"))
        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

        cancel(assignmentId)

        verify(extOrderServiceAdapter).cancelOrder(orderId)

        assertTrue(taskScheduler.getPeriodicScheduledTasks().isEmpty())

        assertAllAssignmentsCancelled(
            path = "/assignment/top-price/",
            expectedCount = 1
        )
        assertAllOrdersCancelled(1)
    }
}
