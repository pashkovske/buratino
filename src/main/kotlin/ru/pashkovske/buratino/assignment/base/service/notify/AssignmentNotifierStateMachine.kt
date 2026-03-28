package ru.pashkovske.buratino.assignment.base.service.notify

import ru.pashkovske.buratino.assignment.base.exception.scheduling.AssignmentNotifyException
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState

object AssignmentNotifierStateMachine {

    val initialState: SchedulingState = SchedulingState.QUEUED

    fun canActive(state: SchedulingState): Boolean {
        return state == SchedulingState.QUEUED
    }

    fun toActive(notifier: AssignmentScheduling) {
        if (!canActive(notifier.state)) {
            throw AssignmentNotifyException(
                message = "Cannot transition from ${notifier.state} to ACTIVE",
                notifierId = notifier.id,
                assignmentId = notifier.assignmentId
            )
        }
        notifier.state = SchedulingState.ACTIVE
    }

    fun canComplete(state: SchedulingState): Boolean {
        return state == SchedulingState.ACTIVE
    }

    fun toComplete(notifier: AssignmentScheduling) {
        if (!canComplete(notifier.state)) {
            throw AssignmentNotifyException(
                message = "Cannot transition from ${notifier.state} to COMPLETED",
                notifierId = notifier.id,
                assignmentId = notifier.assignmentId
            )
        }
        notifier.state = SchedulingState.COMPLETED
    }
}
