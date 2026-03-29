package ru.pashkovske.buratino.assignment.service.notify

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class ContinueNotifyOrchestrator<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment
    >(
    taskScheduler: TaskScheduler,
    private val continuer: ContinuousAssignmentContinuer<ContinuousA>
) : BasicNotifyOrchestrator<ContinuousA>(
    taskScheduler = taskScheduler
) {

    override fun getSubscriber(assignmentId: UUID): AssignmentSchedulingSubscriber {
        return AssignmentSchedulingSubscriber(
            action = continuer::continueAssignment,
            assignmentId = assignmentId
        )
    }
}