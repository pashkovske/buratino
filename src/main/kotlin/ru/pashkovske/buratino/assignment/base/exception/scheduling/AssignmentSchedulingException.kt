package ru.pashkovske.buratino.assignment.base.exception.scheduling

import java.util.UUID

class AssignmentSchedulingException(
    message: String,
    schedulingId: UUID?,
    assignmentId: UUID?,
    cause: Throwable? = null
): RuntimeException(
    parentMessage(
        message = message,
        schedulingId = schedulingId,
        assignmentId = assignmentId
    ),
    cause
) {

    companion object {
        private fun parentMessage(
            message: String,
            schedulingId: UUID?,
            assignmentId: UUID?
        ): String {
            return """
            Scheduling id: $schedulingId
            Assignment id: $assignmentId
            Error:
            ${message.prependIndent("    ")}
        """.trimIndent()
        }
    }
}
