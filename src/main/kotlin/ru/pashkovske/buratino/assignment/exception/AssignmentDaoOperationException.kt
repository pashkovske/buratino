package ru.pashkovske.buratino.assignment.exception

import java.util.UUID

class AssignmentDaoOperationException(
    message: String,
    assignmentId: UUID?
): IllegalArgumentException(
    """
        Assignment id: $assignmentId
        Message: $message
    """.trimIndent()
)