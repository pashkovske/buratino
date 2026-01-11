package ru.pashkovske.buratino.assignment.base.exe

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus

object StatusStateMachine {

    fun isStartable(assignment: Assignment): Boolean {
        return assignment.status == AssignmentStatus.QUEUED
    }

    fun isCompletable(assignment: Assignment): Boolean {
        return assignment.status == AssignmentStatus.IN_PROGRESS
    }

    fun toInProgress(assignment: Assignment) {
        if (!isStartable(assignment)) {
            throw IllegalStateException("Can't set status to IN_PROGRESS from ${assignment.status}")
        }
        assignment.status = AssignmentStatus.IN_PROGRESS
    }

    fun toCompleted(assignment: Assignment) {
        if (!isCompletable(assignment)) {
            throw IllegalStateException("Can't set status to COMPLETED from ${assignment.status}")
        }
        assignment.status = AssignmentStatus.COMPLETED
    }
}
