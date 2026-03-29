package ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.service.notify.BasicNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
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
