package ru.pashkovske.buratino.assignment.service.notify

import ru.pashkovske.buratino.assignment.exception.AssignmentNotifyException
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.NotifierState

object AssignmentNotifierStateMachine {

    val initialState: NotifierState = NotifierState.QUEUED

    fun canActive(state: NotifierState): Boolean {
        return state == NotifierState.QUEUED
    }

    fun toActive(notifier: AssignmentNotifier) {
        if (!canActive(notifier.state)) {
            throw AssignmentNotifyException(
                message = "Cannot transition from ${notifier.state} to ACTIVE",
                notifierId = notifier.id,
                assignmentId = notifier.assignmentId
            )
        }
        notifier.state = NotifierState.ACTIVE
    }

    fun canComplete(state: NotifierState): Boolean {
        return state == NotifierState.ACTIVE
    }

    fun toComplete(notifier: AssignmentNotifier) {
        if (!canComplete(notifier.state)) {
            throw AssignmentNotifyException(
                message = "Cannot transition from ${notifier.state} to COMPLETED",
                notifierId = notifier.id,
                assignmentId = notifier.assignmentId
            )
        }
        notifier.state = NotifierState.COMPLETED
    }
}
