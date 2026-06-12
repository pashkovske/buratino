package ru.pashkovske.buratino.assignment.service.notify.recovery

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifierSubscriber
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.refresh.dispatcher.RefreshDispatcher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

@Service
class RefreshNotifyStartupRecovery(
    refreshNotifierDao: RefreshNotifierDao,
    taskScheduler: TaskScheduler,
    private val refreshDispatcher: RefreshDispatcher
) : NotifyStartupRecovery(
    notifierDao = refreshNotifierDao,
    taskScheduler = taskScheduler
) {

    override fun getSubscriber(assignmentId: UUID): AssignmentNotifierSubscriber {
        val refresher: AssignmentRefresher<*> = refreshDispatcher.getRefresher(assignmentId)
        val subscriber = AssignmentNotifierSubscriber(
            action = { id: UUID -> refresher.refresh(id) },
            assignmentId = assignmentId
        )
        return subscriber
    }
}