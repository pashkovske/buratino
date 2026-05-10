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
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

class FractionalSpreadAssignmentRefreshTest(
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

    @MockitoSpyBean
    private lateinit var extOrderServiceAdapter: ExtOrderServiceAdapter

    @AfterEach
    fun cleanUp() {
        orderDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
    }

    @Test
    fun `skip refresh when price unchanged`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")
        val direction = OrderDirection.BUY
        val rate = 0.007

        val result: MvcResult = create(
            iid = iid,
            direction = direction,
            rate = rate,
        )
        val assignmentId: UUID = UUID.fromString(JsonPath.parse(result.response.contentAsString).read("$.id"))

        refresh(assignmentId)

        Mockito.verify(extOrderServiceAdapter, Mockito.never()).replaceOrder(any(), any())
    }
}