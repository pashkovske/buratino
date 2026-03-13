package ru.pashkovske.buratino.assignment.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState

object AssignmentStateMachine {

    fun isStartable(assignment: Assignment): Boolean {
        return assignment.state == AssignmentState.QUEUED
    }

    fun isCompletable(assignment: Assignment): Boolean {
        return assignment.state == AssignmentState.IN_PROGRESS
    }

    fun toInProgress(assignment: Assignment) {
        if (!isStartable(assignment)) {
            throw IllegalStateException("Can't set state to IN_PROGRESS from ${assignment.state}")
        }
        assignment.state = AssignmentState.IN_PROGRESS
    }

    fun toCompleted(assignment: Assignment) {
        if (!isCompletable(assignment)) {
            throw IllegalStateException("Can't set state to COMPLETED from ${assignment.state}")
        }
        assignment.state = AssignmentState.COMPLETED
    }
}
