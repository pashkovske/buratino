package ru.pashkovske.buratino.assignment.base.repo

import java.util.UUID

class AssignmentRepoOperationException(
    message: String,
    assignmentId: UUID
): IllegalArgumentException(
    """
        Assignment id: $assignmentId
        Message: $message
    """.trimIndent()
)
