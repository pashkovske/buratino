package ru.pashkovske.buratino.integration.assignment.parent.continuous

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import ru.pashkovske.buratino.assignment.dao.core.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.core.postgre.RepeatableFractionalSpreadAssignmentDao
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID
import kotlin.test.assertTrue

class RepeatableFractionalSpreadAssignmentCancelTest(
    @Autowired mockMvc: MockMvc
) : RepeatableFractionalSpreadTest(
    mockMvc = mockMvc
) {

    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper

    @Autowired
    private lateinit var repeatableFractionalSpreadAssignmentDao: RepeatableFractionalSpreadAssignmentDao

    @Autowired
    private lateinit var fractionalSpreadAssignmentDao: FractionalSpreadAssignmentDao

    @Autowired
    private lateinit var orderDao: OrderDao

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
        repeatableFractionalSpreadAssignmentDao.deleteAll()
    }

    @Test
    fun `cancel repeatable fractional spread assignment`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.BUY
        val rate = 0.007

        val result: MvcResult = create(
            iid = iid,
            direction = direction,
            rate = rate,
        )
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(result.response.contentAsString).read("$.id"))
        val orderId: String = JsonPath.parse(result.response.contentAsString).read("$.child.info.orderId")

        cancel(assignmentId)

        Mockito.verify(extOrderServiceAdapter).cancelOrder(orderId)
        assertTrue(taskScheduler.getPeriodicScheduledTasks().isEmpty())

        assertAllAssignmentsCancelled(
            path = "/assignment/repeatable/fractional-spread/",
            expectedCount = 1
        )
        assertAllOrdersCancelled(1)
    }
}
