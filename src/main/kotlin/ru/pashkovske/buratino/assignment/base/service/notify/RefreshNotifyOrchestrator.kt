package ru.pashkovske.buratino.assignment.base.service.notify

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class RefreshNotifyOrchestrator<A : Assignment>(
    taskScheduler: TaskScheduler,
    private val refresher: AssignmentRefresher<A>
) : BasicNotifyOrchestrator<A>(
    taskScheduler = taskScheduler
) {

    override fun getSubscriber(assignmentId: UUID): AssignmentSchedulingSubscriber {
        return AssignmentSchedulingSubscriber(
            action = refresher::refresh,
            assignmentId = assignmentId
        )
    }
}
