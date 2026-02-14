package ru.pashkovske.buratino.integration.assignment

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.limit.top.price.dao.postgre.TopPriceAssignmentDao
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.dao.OrderDao

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(IntegrationStubsConfiguration::class)
@SpringBootTest
class RecoverAssignmentsTest(
    @param:Autowired private val mockMvc: MockMvc
) {

    @Autowired
    private lateinit var assignmentExe: AssignmentExe<TopPriceAssignment>
    @Autowired
    private lateinit var topPriceAssignmentDao: TopPriceAssignmentDao
    @Autowired
    private lateinit var orderDao: OrderDao
    @Autowired
    private lateinit var assignmentTaskScheduler: AssignmentTaskScheduler
    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        orderDao.deleteAll()
        topPriceAssignmentDao.deleteAll()
        shutdownScheduler()
    }

    private fun shutdownScheduler() {
        assignmentTaskScheduler.getScheduled().toList().forEach { taskId ->
            assignmentTaskScheduler.stop(taskId)
        }
    }

    @Test
    fun `recoverAssignments should restore refresh scheduling for IN_PROGRESS assignments`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")

        // Create assignment 1: IN_PROGRESS with refresh scheduling
        val createResult1: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"refreshSchedulingInterval\": \"PT10M\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1Id: String = objectMapper.readTree(createResult1.response.contentAsString)
            .get("id").asText()

        // Create assignment 2: IN_PROGRESS without refresh scheduling
        val createResult2: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment2Id: String = objectMapper.readTree(createResult2.response.contentAsString)
            .get("id").asText()

        // Create assignment 3: IN_PROGRESS with refresh scheduling, then cancel it
        val createResult3: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"refreshSchedulingInterval\": \"PT15M\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment3Id: String = objectMapper.readTree(createResult3.response.contentAsString)
            .get("id").asText()
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/assignment/top-price/{id}", assignment3Id)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())

        assertEquals(1, assignmentTaskScheduler.getScheduled().size)

        val getResult1: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment1Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1BeforeRestart = objectMapper.readValue(
            getResult1.response.contentAsString,
            TopPriceAssignment::class.java
        )
        val schedulingTaskIdBeforeRestart = assignment1BeforeRestart.getRefreshSchedulingInfo()?.taskId
        assertNotNull(schedulingTaskIdBeforeRestart)
        assertTrue(assignmentTaskScheduler.getScheduled().contains(schedulingTaskIdBeforeRestart))

        shutdownScheduler()
        assertEquals(0, assignmentTaskScheduler.getScheduled().size)
        assignment1BeforeRestart.clearRefreshScheduling()
        topPriceAssignmentDao.update(assignment1BeforeRestart)
        val getResult2: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment2BeforeRestart = objectMapper.readValue(
            getResult2.response.contentAsString,
            TopPriceAssignment::class.java
        )
        assertNull(assignment2BeforeRestart.getRefreshSchedulingInfo())

        val recoveredAssignments: List<TopPriceAssignment> = assignmentExe.recoverAssignments()

        assertEquals(2, recoveredAssignments.size)
        assertTrue(recoveredAssignments.any { it.id.toString() == assignment1Id })
        assertTrue(recoveredAssignments.any { it.id.toString() == assignment2Id })

        val scheduledCountAfter = assignmentTaskScheduler.getScheduled().size
        assertEquals(1, scheduledCountAfter)

        val getResult1After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment1Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1AfterRecover: TopPriceAssignment = objectMapper.readValue(
            getResult1After.response.contentAsString,
            TopPriceAssignment::class.java
        )
        val schedulingInfoAfterRecover: SchedulingInfo? = assignment1AfterRecover.getRefreshSchedulingInfo()
        assertNotNull(schedulingInfoAfterRecover)
        assertTrue(schedulingTaskIdBeforeRestart != schedulingInfoAfterRecover!!.taskId)
        assertEquals(
            assignmentTaskScheduler.getScheduled().first(),
            schedulingInfoAfterRecover.taskId
        )

        val getResult2After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment2AfterRecover = objectMapper.readValue(
            getResult2After.response.contentAsString,
            TopPriceAssignment::class.java
        )
        assertNull(assignment2AfterRecover.getRefreshSchedulingInfo())
        assertEquals(AssignmentStatus.IN_PROGRESS, assignment2AfterRecover.status)

        val getResult3After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment3Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment3AfterRecover = objectMapper.readValue(
            getResult3After.response.contentAsString,
            TopPriceAssignment::class.java
        )
        assertNotNull(assignment3AfterRecover.getRefreshSchedulingInfo())
        assertEquals(SchedulingStatus.COMPLETED, assignment3AfterRecover.getRefreshSchedulingInfo()!!.status)
    }
}
