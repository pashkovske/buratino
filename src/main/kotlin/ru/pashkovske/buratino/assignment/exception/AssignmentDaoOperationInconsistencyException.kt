package ru.pashkovske.buratino.assignment.exception

import java.util.UUID

class AssignmentDaoOperationInconsistencyException(
    message: String,
    assignmentId: UUID?
): AssignmentDaoOperationException(
    message = message,
    assignmentId = assignmentId
)
