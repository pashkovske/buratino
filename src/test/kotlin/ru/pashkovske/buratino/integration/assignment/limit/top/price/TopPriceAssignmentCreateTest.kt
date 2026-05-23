package ru.pashkovske.buratino.integration.assignment.limit.top.price

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import ru.pashkovske.buratino.assignment.dao.core.postgre.TopPriceAssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.service.core.refresh.TopPriceAssignmentRefresher
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

class TopPriceAssignmentCreateTest(
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

    @Autowired
    private lateinit var refreshNotifierDao: RefreshNotifierDao

    @MockitoSpyBean
    private lateinit var refresher: TopPriceAssignmentRefresher

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        topPriceAssignmentDao.deleteAll()
    }

    @ParameterizedTest
    @MethodSource("orderDirectionAndStepOver")
    fun `create share with one step over`(
        orderDirection: OrderDirection,
        oneStepOver: Boolean
    ) {
        val iid: InstrumentId = bootstrapper.getIid("kzos")

        val result: MvcResult = create(
            iid = iid,
            direction = orderDirection,
            oneStepOver = oneStepOver,
        )

        Mockito.verify(extOrderServiceAdapter).createOrder(any())

        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.info.orderId")

        val priceStep = if (oneStepOver) {
            Price(
                unit = 0,
                nano = 100_000_000,
                currency = Currency.RUB
            )
        } else {
            Price(
                unit = 0,
                nano = 0,
                currency = Currency.RUB
            )
        }
        val expectedPrice = when (orderDirection) {
            OrderDirection.SELL -> Price(
                unit = 66,
                nano = 200_000_000,
                currency = Currency.RUB
            ) - priceStep

            OrderDirection.BUY -> Price(
                unit = 65,
                nano = 500_000_000,
                currency = Currency.RUB
            ) + priceStep
        }
        val expectedOrderRequest = LimitOrderRequest(
            iid = iid,
            direction = orderDirection,
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
    fun `create with refresh notifier triggers refresh periodically`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.SELL
        val oneStepOver = true

        val createResult: MvcResult = create(
            iid = iid,
            direction = direction,
            oneStepOver = oneStepOver,
            refreshPeriod = "PT0.1S"
        )

        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)
        Thread.sleep(290)
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        verify(refresher, atLeast(2)).refresh(eq(assignmentId))
    }
}