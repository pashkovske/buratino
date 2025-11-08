package ru.pashkovske.buratino.assignment.base.model.scheduling

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionChain

class SchedulingAssignmentTask<T: Assignment>(
    private val actionChain: AssignmentActionChain<T>,
    private val assignment: T
): Runnable {
    override fun run() {
        actionChain(assignment)
    }
}
