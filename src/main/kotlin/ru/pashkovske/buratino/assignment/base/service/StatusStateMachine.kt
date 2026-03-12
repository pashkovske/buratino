package ru.pashkovske.buratino.assignment.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState

object StatusStateMachine {

    fun isStartable(assignment: Assignment): Boolean {
        return assignment.status == AssignmentState.QUEUED
    }

    fun isCompletable(assignment: Assignment): Boolean {
        return assignment.status == AssignmentState.IN_PROGRESS
    }

    fun toInProgress(assignment: Assignment) {
        if (!isStartable(assignment)) {
            throw IllegalStateException("Can't set status to IN_PROGRESS from ${assignment.status}")
        }
        assignment.status = AssignmentState.IN_PROGRESS
    }

    fun toCompleted(assignment: Assignment) {
        if (!isCompletable(assignment)) {
            throw IllegalStateException("Can't set status to COMPLETED from ${assignment.status}")
        }
        assignment.status = AssignmentState.COMPLETED
    }
}
