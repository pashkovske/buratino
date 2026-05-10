package ru.pashkovske.buratino.integration.assignment.limit.fractional.spread

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import ru.pashkovske.buratino.assignment.dao.core.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FractionalSpreadAssignmentCreateTest(
    @Autowired mockMvc: MockMvc
) : FractionalSpreadTest(
    mockMvc = mockMvc
) {

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var fractionalSpreadAssignmentDao: FractionalSpreadAssignmentDao
    @Autowired
    private lateinit var orderDao: OrderDao
    @Autowired
    private lateinit var refreshNotifierDao: RefreshNotifierDao

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
    }

    @Test
    fun `create buy with rate`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.BUY
        val rate = 0.007

        val result: MvcResult = create(
            iid = iid,
            direction = direction,
            rate = rate,
        )

        Mockito.verify(extOrderServiceAdapter).createOrder(any())

        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

        val expectedPrice = Price(
            unit = 65,
            nano = 600_000_000,
            currency = Currency.RUB
        )
        val expectedOrderRequest = LimitOrderRequest(
            iid = iid,
            direction = direction,
            lots = 1,
            idempotencyToken = null,
            price = expectedPrice
        )
        expectOrder(
            orderId = orderId,
            expected = expectedOrderRequest
        )
    }

    @Test
    fun `create with refresh notifier`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.BUY
        val rate = 0.007

        val createResult: MvcResult = create(
            iid = iid,
            direction = direction,
            rate = rate,
            refreshPeriod = "PT10M"
        )

        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: FractionalSpreadAssignment = fractionalSpreadAssignmentDao.get(assignmentId)
        val refreshNotifierId: UUID? = assignment.getRefreshNotifierId()
        assertNotNull(refreshNotifierId)
        val refreshNotifier: AssignmentNotifier = refreshNotifierDao.get(refreshNotifierId)
        assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            refreshNotifier.taskId
        )
    }
}