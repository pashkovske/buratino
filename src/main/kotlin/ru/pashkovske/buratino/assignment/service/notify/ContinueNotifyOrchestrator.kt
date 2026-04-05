package ru.pashkovske.buratino.assignment.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.notify.ContinueNotifierDao
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifierSubscriber
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.service.core.continuation.dispatcher.ContinueDispatcher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

@Service
class ContinueNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    notifierDao: ContinueNotifierDao,
    private val continueDispatcher: ContinueDispatcher
) : BasicNotifyOrchestrator(
    taskScheduler = taskScheduler,
    notifierDao = notifierDao
) {

    override fun getSubscriber(assignmentId: UUID): AssignmentNotifierSubscriber {
        val continuer: ContinuousAssignmentContinuer<*> = continueDispatcher.getContinuer(assignmentId)
        return AssignmentNotifierSubscriber(
            action = continuer::continueAssignment,
            assignmentId = assignmentId
        )
    }
}