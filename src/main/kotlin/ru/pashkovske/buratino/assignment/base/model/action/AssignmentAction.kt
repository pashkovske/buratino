package ru.pashkovske.buratino.assignment.base.model.action

import ru.pashkovske.buratino.assignment.base.model.Assignment

data class AssignmentAction<A : Assignment>(
    val name: String,
    val action: (A) -> AssignmentActionResult<A>
) {
    operator fun invoke(assignment: A): AssignmentActionResult<A> {
        return action(assignment)
    }
}
