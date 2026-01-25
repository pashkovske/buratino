package ru.pashkovske.buratino.assignment.base.repo

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID
import kotlin.reflect.KClass

class AssignmentRepoOperationException(
    message: String,
    assignmentId: UUID,
    assignmentClass: KClass<out Assignment>
): IllegalArgumentException(
    """
        Assignment type: ${assignmentClass.simpleName}
        Assignment id: $assignmentId
        Message: $message
    """.trimIndent()
)
