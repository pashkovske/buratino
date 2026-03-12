package ru.pashkovske.buratino.integration.assignment

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.limit.top.price.dao.postgre.TopPriceAssignmentDao
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.dao.postgre.ContinuousFractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
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
    private lateinit var continuousAssignmentExe: AssignmentExe<ContinuousFractionalSpreadAssignment>
    @Autowired
    private lateinit var topPriceAssignmentDao: TopPriceAssignmentDao
    @Autowired
    private lateinit var continuousFractionalSpreadAssignmentDao: ContinuousFractionalSpreadAssignmentDao
    @Autowired
    private lateinit var fractionalSpreadAssignmentDao: FractionalSpreadAssignmentDao
    @Autowired
    private lateinit var orderDao: OrderDao
    @Autowired
    private lateinit var taskScheduler: TaskScheduler
    @Autowired
    private lateinit var bootstrapper: AssignmentTestBootstrapper
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @AfterEach
    fun setUp() {
        orderDao.deleteAll()
        topPriceAssignmentDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
        continuousFractionalSpreadAssignmentDao.deleteAll()
        shutdownScheduler()
    }

    private fun shutdownScheduler() {
        taskScheduler.getPeriodicScheduledTasks().toList().forEach { taskId ->
            taskScheduler.stopPeriodic(taskId)
        }
    }

    @Test
    fun `recoverAssignments should recover refresh scheduling for IN_PROGRESS assignments`() {
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

        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)

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
        assertTrue(taskScheduler.getPeriodicScheduledTasks().contains(schedulingTaskIdBeforeRestart))

        shutdownScheduler()
        assertEquals(0, taskScheduler.getPeriodicScheduledTasks().size)
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

        val scheduledCountAfter = taskScheduler.getPeriodicScheduledTasks().size
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
            taskScheduler.getPeriodicScheduledTasks().first(),
            schedulingInfoAfterRecover.taskId
        )

        val getResult2After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment2AfterRecover: TopPriceAssignment = objectMapper.readValue(
            getResult2After.response.contentAsString,
            TopPriceAssignment::class.java
        )
        assertNull(assignment2AfterRecover.getRefreshSchedulingInfo())
        assertEquals(AssignmentState.IN_PROGRESS, assignment2AfterRecover.status)

        val getResult3After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment3Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment3AfterRecover: TopPriceAssignment = objectMapper.readValue(
            getResult3After.response.contentAsString,
            TopPriceAssignment::class.java
        )
        assertNotNull(assignment3AfterRecover.getRefreshSchedulingInfo())
        assertEquals(SchedulingState.COMPLETED, assignment3AfterRecover.getRefreshSchedulingInfo()!!.status)
    }

    @Test
    fun `recoverAssignments should recover continue scheduling for IN_PROGRESS continuous assignments`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")

        // Create assignment 1: IN_PROGRESS with continue scheduling
        val createResult1: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"rate\": 0.007, \"continueSchedulingInterval\": \"PT10M\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1Id: String = objectMapper.readTree(createResult1.response.contentAsString)
            .get("id").asText()

        // Create assignment 2: IN_PROGRESS without continue scheduling
        val createResult2: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"rate\": 0.007}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment2Id: String = objectMapper.readTree(createResult2.response.contentAsString)
            .get("id").asText()

        // Create assignment 3: IN_PROGRESS with continue scheduling, then cancel it
        val createResult3: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"rate\": 0.007, \"continueSchedulingInterval\": \"PT15M\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment3Id: String = objectMapper.readTree(createResult3.response.contentAsString)
            .get("id").asText()
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/assignment/continuous/fractional-spread/{id}", assignment3Id)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())

        assertEquals(1, taskScheduler.getPeriodicScheduledTasks().size)

        val getResult1: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment1Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1BeforeRestart = objectMapper.readValue(
            getResult1.response.contentAsString,
            ContinuousFractionalSpreadAssignment::class.java
        )
        val schedulingTaskIdBeforeRestart = assignment1BeforeRestart.getContinueSchedulingInfo()?.taskId
        assertNotNull(schedulingTaskIdBeforeRestart)
        assertTrue(taskScheduler.getPeriodicScheduledTasks().contains(schedulingTaskIdBeforeRestart))

        shutdownScheduler()
        assertEquals(0, taskScheduler.getPeriodicScheduledTasks().size)
        assignment1BeforeRestart.clearContinueScheduling()
        continuousFractionalSpreadAssignmentDao.update(assignment1BeforeRestart)
        val getResult2: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment2BeforeRestart = objectMapper.readValue(
            getResult2.response.contentAsString,
            ContinuousFractionalSpreadAssignment::class.java
        )
        assertNull(assignment2BeforeRestart.getContinueSchedulingInfo())

        val recoveredAssignments: List<ContinuousFractionalSpreadAssignment> = continuousAssignmentExe.recoverAssignments()

        assertEquals(2, recoveredAssignments.size)
        assertTrue(recoveredAssignments.any { it.id.toString() == assignment1Id })
        assertTrue(recoveredAssignments.any { it.id.toString() == assignment2Id })

        val scheduledCountAfter: Int = taskScheduler.getPeriodicScheduledTasks().size
        assertEquals(1, scheduledCountAfter)

        val getResult1After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment1Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1AfterRecover: ContinuousFractionalSpreadAssignment = objectMapper.readValue(
            getResult1After.response.contentAsString,
            ContinuousFractionalSpreadAssignment::class.java
        )
        val schedulingInfoAfterRecover: SchedulingInfo? = assignment1AfterRecover.getContinueSchedulingInfo()
        assertNotNull(schedulingInfoAfterRecover)
        assertTrue(schedulingTaskIdBeforeRestart != schedulingInfoAfterRecover!!.taskId)
        assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            schedulingInfoAfterRecover.taskId
        )

        val getResult2After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment2AfterRecover: ContinuousFractionalSpreadAssignment = objectMapper.readValue(
            getResult2After.response.contentAsString,
            ContinuousFractionalSpreadAssignment::class.java
        )
        assertNull(assignment2AfterRecover.getContinueSchedulingInfo())
        assertEquals(AssignmentState.IN_PROGRESS, assignment2AfterRecover.status)

        val getResult3After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment3Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment3AfterRecover: ContinuousFractionalSpreadAssignment = objectMapper.readValue(
            getResult3After.response.contentAsString,
            ContinuousFractionalSpreadAssignment::class.java
        )
        assertNotNull(assignment3AfterRecover.getContinueSchedulingInfo())
        assertEquals(SchedulingState.COMPLETED, assignment3AfterRecover.getContinueSchedulingInfo()!!.status)
    }
}
