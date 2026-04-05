package ru.pashkovske.buratino.assignment.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifierSubscriber
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.refresh.dispatcher.RefreshDispatcher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

@Service
class RefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    notifierDao: RefreshNotifierDao,
    private val refreshDispatcher: RefreshDispatcher
) : BasicNotifyOrchestrator(
    taskScheduler = taskScheduler,
    notifierDao = notifierDao
) {

    override fun getSubscriber(assignmentId: UUID): AssignmentNotifierSubscriber {
        val refresher: AssignmentRefresher<out Assignment> = refreshDispatcher.getRefresher(assignmentId)
        return AssignmentNotifierSubscriber(
            action = refresher::refresh,
            assignmentId = assignmentId
        )
    }
}
