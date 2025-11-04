package ru.pashkovske.buratino.assignment.base.model.action

import ru.pashkovske.buratino.assignment.base.model.Assignment

data class AssignmentActionResult<A : Assignment>(
    val assignment: A,
    val shouldContinue: Boolean
)
