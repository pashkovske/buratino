package ru.pashkovske.buratino.integration.assignment.parent.continuous

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import ru.pashkovske.buratino.assignment.dao.core.postgre.ContinuousFractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.core.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.ContinueNotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.service.core.refresh.ContinuousFractionalSpreadAssignmentRefresher
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

class ContinuousFractionalSpreadAssignmentCreateTest(
    @Autowired mockMvc: MockMvc
) : ContinuousFractionalSpreadTest(
    mockMvc = mockMvc
) {

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper

    @Autowired
    private lateinit var continuousFractionalSpreadAssignmentDao: ContinuousFractionalSpreadAssignmentDao

    @Autowired
    private lateinit var fractionalSpreadAssignmentDao: FractionalSpreadAssignmentDao

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var refreshNotifierDao: RefreshNotifierDao

    @Autowired
    private lateinit var continueNotifierDao: ContinueNotifierDao

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @MockitoSpyBean
    private lateinit var refresher: ContinuousFractionalSpreadAssignmentRefresher

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
        continuousFractionalSpreadAssignmentDao.deleteAll()
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

        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.child.info.orderId")

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
    fun `create with continue notifier`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val rate = 0.007

        val createResult: MvcResult = create(
            iid = iid,
            direction = direction,
            rate = rate,
            continuePeriod = "PT10M"
        )

        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val assignment: ContinuousFractionalSpreadAssignment = continuousFractionalSpreadAssignmentDao.get(assignmentId)
        val continueNotifierId: UUID? = assignment.getContinueNotifierId()
        assertNotNull(continueNotifierId)
        val continueNotifier: AssignmentNotifier = continueNotifierDao.get(continueNotifierId)
        assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            continueNotifier.taskId
        )
    }

    @Test
    fun `create with refresh notifier triggers refresh periodically`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val rate = 0.007

        val createResult: MvcResult = create(
            iid = iid,
            direction = direction,
            rate = rate,
            refreshPeriod = "PT0.1S"
        )

        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        Thread.sleep(290)
        verify(refresher, atLeast(2)).refresh(eq(assignmentId))
    }
}