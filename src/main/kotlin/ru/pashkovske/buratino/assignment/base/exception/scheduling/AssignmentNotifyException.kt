package ru.pashkovske.buratino.assignment.base.exception.scheduling

import java.util.UUID

class AssignmentNotifyException(
    message: String,
    notifierId: UUID?,
    assignmentId: UUID?,
    cause: Throwable? = null
): RuntimeException(
    parentMessage(
        message = message,
        notifierId = notifierId,
        assignmentId = assignmentId
    ),
    cause
) {

    companion object {
        private fun parentMessage(
            message: String,
            notifierId: UUID?,
            assignmentId: UUID?
        ): String {
            return """
            Notifier id: $notifierId
            Assignment id: $assignmentId
            Error:
            ${message.prependIndent("    ")}
        """.trimIndent()
        }
    }
}
