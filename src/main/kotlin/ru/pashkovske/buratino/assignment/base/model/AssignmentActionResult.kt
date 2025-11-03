package ru.pashkovske.buratino.assignment.base.model

data class AssignmentActionResult<A : Assignment>(
    val assignment: A,
    val shouldContinue: Boolean
)
