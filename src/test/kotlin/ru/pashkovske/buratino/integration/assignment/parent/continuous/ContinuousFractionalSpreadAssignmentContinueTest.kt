package ru.pashkovske.buratino.integration.assignment.parent.continuous

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import ru.pashkovske.buratino.assignment.dao.core.postgre.ContinuousFractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.core.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID
import kotlin.test.assertNotEquals

class ContinuousFractionalSpreadAssignmentContinueTest(
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

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
        continuousFractionalSpreadAssignmentDao.deleteAll()
    }

    @Test
    fun `continue assignment after child completion`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.BUY
        val rate = 0.007

        // Create continuous assignment
        val createResult: MvcResult = create(
            iid = iid,
            direction = direction,
            rate = rate,
        )
        
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.id"))
        val childAssignmentId: UUID = UUID.fromString(JsonPath.parse(createResult.response.contentAsString).read("$.child.id"))
        val createdOrderId: String = JsonPath.parse(createResult.response.contentAsString).read("$.child.info.orderId")

        // Cancel child to simulate completion
        performAndCheckCancel(
            path = "/assignment/fractional-spread/{assignmentId}",
            assignmentId = childAssignmentId,
            iid = iid
        )

        Mockito.verify(extOrderServiceAdapter).cancelOrder(createdOrderId)

        // Continue parent assignment
        val continueResult: MvcResult = continueAssignment(assignmentId)
        
        val continuedOrderId: String = JsonPath.parse(continueResult.response.contentAsString).read("$.child.info.orderId")
        
        // Verify new order was created with opposite direction
        assertNotEquals(continuedOrderId, createdOrderId)
        Mockito.verify(extOrderServiceAdapter, Mockito.times(2)).createOrder(any())
    }
}