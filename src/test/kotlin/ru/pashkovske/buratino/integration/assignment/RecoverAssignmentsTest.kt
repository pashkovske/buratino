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
import ru.pashkovske.buratino.assignment.controller.dto.ContinuousFractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.controller.dto.TopPriceAssignmentDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentNotifierDto
import ru.pashkovske.buratino.assignment.dao.core.postgre.ContinuousFractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.core.postgre.FractionalSpreadAssignmentDao
import ru.pashkovske.buratino.assignment.dao.core.postgre.TopPriceAssignmentDao
import ru.pashkovske.buratino.assignment.dao.notify.ContinueNotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.TopPriceAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.NotifierState
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.integration.configuration.IntegrationStubsConfiguration
import ru.pashkovske.buratino.integration.mock.bootstrapper.AssignmentTestBootstrapper
import ru.pashkovske.buratino.order.dao.OrderDao
import java.util.UUID

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(IntegrationStubsConfiguration::class)
@SpringBootTest
class RecoverAssignmentsTest(
    @param:Autowired private val mockMvc: MockMvc
) {

    @Autowired
    private lateinit var assignmentExe: AssignmentExe<TopPriceAssignment, TopPriceAssignmentStartCmd>

    @Autowired
    private lateinit var continuousAssignmentExe: AssignmentExe<ContinuousFractionalSpreadAssignment, ContinuousFractionalSpreadAssignmentStartCmd>

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

    @Autowired
    private lateinit var refreshNotifyOrchestrator: RefreshNotifyOrchestrator

    @Autowired
    private lateinit var continueNotifyOrchestrator: ContinueNotifyOrchestrator

    @Autowired
    private lateinit var refreshNotifierDao: RefreshNotifierDao

    @Autowired
    private lateinit var continueNotifierDao: ContinueNotifierDao

    @AfterEach
    fun clean() {
        orderDao.deleteAll()
        topPriceAssignmentDao.deleteAll()
        fractionalSpreadAssignmentDao.deleteAll()
        continuousFractionalSpreadAssignmentDao.deleteAll()
        taskScheduler.shutdown()
        refreshNotifierDao.deleteAll()
        continueNotifierDao.deleteAll()
    }

    @Test
    fun `recoverAssignments should recover refresh notifier for IN_PROGRESS assignments`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")

        // Create assignment 1: IN_PROGRESS with refresh notifier
        val createResult1: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"refreshNotifyPeriod\": \"PT10M\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1Id: String = objectMapper.readTree(createResult1.response.contentAsString)
            .get("id").asText()

        // Create assignment 2: IN_PROGRESS without refresh notifier
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

        // Create assignment 3: IN_PROGRESS with refresh notifier, then cancel it
        val createResult3: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/top-price/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"refreshNotifyPeriod\": \"PT15M\"}")
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
        val assignmentDto1BeforeRestart: TopPriceAssignmentDto = objectMapper.readValue(
            getResult1.response.contentAsString,
            TopPriceAssignmentDto::class.java
        )
        val assignment1BeforeRestart: TopPriceAssignment = topPriceAssignmentDao.get(UUID.fromString(assignment1Id))
        val notifierTaskIdBeforeRestart: UUID? = assignmentDto1BeforeRestart.refreshNotifier?.taskId
        assertNotNull(notifierTaskIdBeforeRestart)
        assertTrue(taskScheduler.getPeriodicScheduledTasks().contains(notifierTaskIdBeforeRestart))

        taskScheduler.shutdown()
        assertEquals(0, taskScheduler.getPeriodicScheduledTasks().size)
        topPriceAssignmentDao.update(assignment1BeforeRestart)
        val getResult2: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto2BeforeRestart: TopPriceAssignmentDto = objectMapper.readValue(
            getResult2.response.contentAsString,
            TopPriceAssignmentDto::class.java
        )
        assertNull(assignmentDto2BeforeRestart.refreshNotifier)

        val recoveredNotifiers: List<AssignmentNotifier> = refreshNotifyOrchestrator.recoverNotifiers()

        assertEquals(1, recoveredNotifiers.size)
        assertEquals(
            assignment1Id,
            recoveredNotifiers[0].assignmentId.toString()
        )

        val scheduledCountAfter: Int = taskScheduler.getPeriodicScheduledTasks().size
        assertEquals(1, scheduledCountAfter)

        val getResult1After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment1Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto1AfterRecover: TopPriceAssignmentDto = objectMapper.readValue(
            getResult1After.response.contentAsString,
            TopPriceAssignmentDto::class.java
        )
        val assignmentNotifierAfterRecover: AssignmentNotifierDto? = assignmentDto1AfterRecover.refreshNotifier
        assertNotNull(assignmentNotifierAfterRecover)
        assertTrue(notifierTaskIdBeforeRestart != assignmentNotifierAfterRecover!!.taskId)
        assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            assignmentNotifierAfterRecover.taskId
        )

        val getResult2After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto2AfterRecover: TopPriceAssignmentDto = objectMapper.readValue(
            getResult2After.response.contentAsString,
            TopPriceAssignmentDto::class.java
        )
        assertNull(assignmentDto2AfterRecover.refreshNotifier)
        assertEquals(AssignmentState.IN_PROGRESS, assignmentDto2AfterRecover.state)

        val getResult3After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/top-price/{id}", assignment3Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto3AfterRecover: TopPriceAssignmentDto = objectMapper.readValue(
            getResult3After.response.contentAsString,
            TopPriceAssignmentDto::class.java
        )
        assertNotNull(assignmentDto3AfterRecover.refreshNotifier)
        assertEquals(NotifierState.COMPLETED, assignmentDto3AfterRecover.refreshNotifier!!.state)
    }

    @Test
    fun `recoverAssignments should recover continue notifier for IN_PROGRESS continuous assignments`() {
        val iid: InstrumentId = bootstrapper.getIid("kzos")

        // Create assignment 1: IN_PROGRESS with continue notifier
        val createResult1: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"rate\": 0.007, \"continueNotifyPeriod\": \"PT10M\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignment1Id: String = objectMapper.readTree(createResult1.response.contentAsString)
            .get("id").asText()

        // Create assignment 2: IN_PROGRESS without continue notifier
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

        // Create assignment 3: IN_PROGRESS with continue notifier, then cancel it
        val createResult3: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .post(
                    "/assignment/continuous/fractional-spread/{instrumentId}/start/{direction}",
                    iid.id,
                    "sell"
                )
                .header("X-API-KEY", "test-api-key")
                .content("{\"rate\": 0.007, \"continueNotifyPeriod\": \"PT15M\"}")
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
        val assignmentDto1BeforeRestart: ContinuousFractionalSpreadAssignmentDto = objectMapper.readValue(
            getResult1.response.contentAsString,
            ContinuousFractionalSpreadAssignmentDto::class.java
        )
        val assignment1BeforeRestart: ContinuousFractionalSpreadAssignment =
            continuousFractionalSpreadAssignmentDao.get(UUID.fromString(assignment1Id))
        val notifierTaskIdBeforeRestart: UUID? = assignmentDto1BeforeRestart.continueNotifier?.taskId
        assertNotNull(notifierTaskIdBeforeRestart)
        assertTrue(taskScheduler.getPeriodicScheduledTasks().contains(notifierTaskIdBeforeRestart))

        taskScheduler.shutdown()
        assertEquals(0, taskScheduler.getPeriodicScheduledTasks().size)
        continuousFractionalSpreadAssignmentDao.update(assignment1BeforeRestart)
        val getResult2: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto2BeforeRestart: ContinuousFractionalSpreadAssignmentDto = objectMapper.readValue(
            getResult2.response.contentAsString,
            ContinuousFractionalSpreadAssignmentDto::class.java
        )
        assertNull(assignmentDto2BeforeRestart.continueNotifier)

        val recoveredNotifiers: List<AssignmentNotifier> = continueNotifyOrchestrator.recoverNotifiers()

        assertEquals(1, recoveredNotifiers.size)
        assertEquals(
            assignment1Id,
            recoveredNotifiers[0].assignmentId.toString()
        )

        val scheduledCountAfter: Int = taskScheduler.getPeriodicScheduledTasks().size
        assertEquals(1, scheduledCountAfter)

        val getResult1After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment1Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto1AfterRecover: ContinuousFractionalSpreadAssignmentDto = objectMapper.readValue(
            getResult1After.response.contentAsString,
            ContinuousFractionalSpreadAssignmentDto::class.java
        )
        val assignmentNotifierAfterRecover: AssignmentNotifierDto? = assignmentDto1AfterRecover.continueNotifier
        assertNotNull(assignmentNotifierAfterRecover)
        assertTrue(notifierTaskIdBeforeRestart != assignmentNotifierAfterRecover!!.taskId)
        assertEquals(
            taskScheduler.getPeriodicScheduledTasks().first(),
            assignmentNotifierAfterRecover.taskId
        )

        val getResult2After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment2Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto2AfterRecover: ContinuousFractionalSpreadAssignmentDto = objectMapper.readValue(
            getResult2After.response.contentAsString,
            ContinuousFractionalSpreadAssignmentDto::class.java
        )
        assertNull(assignmentDto2AfterRecover.continueNotifier)
        assertEquals(AssignmentState.IN_PROGRESS, assignmentDto2AfterRecover.state)

        val getResult3After: MvcResult = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/assignment/continuous/fractional-spread/{id}", assignment3Id)
                .header("X-API-KEY", "test-api-key")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
        val assignmentDto3AfterRecover: ContinuousFractionalSpreadAssignmentDto = objectMapper.readValue(
            getResult3After.response.contentAsString,
            ContinuousFractionalSpreadAssignmentDto::class.java
        )
        assertNotNull(assignmentDto3AfterRecover.continueNotifier)
        assertEquals(NotifierState.COMPLETED, assignmentDto3AfterRecover.continueNotifier!!.state)
    }
}
