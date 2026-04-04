package ru.pashkovske.buratino.assignment.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.notify.postgre.PostgreContinueNotifierDao
import ru.pashkovske.buratino.assignment.model.notify.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.service.core.continuation.dispatcher.ContinueDispatcher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

@Service
class ContinueNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    notifierDao: PostgreContinueNotifierDao,
    private val continueDispatcher: ContinueDispatcher
) : BasicNotifyOrchestrator(
    taskScheduler = taskScheduler,
    notifierDao = notifierDao
) {

    override fun getSubscriber(assignmentId: UUID): AssignmentSchedulingSubscriber {
        val continuer: ContinuousAssignmentContinuer<*> = continueDispatcher.getContinuer(assignmentId)
        return AssignmentSchedulingSubscriber(
            action = continuer::continueAssignment,
            assignmentId = assignmentId
        )
    }
}