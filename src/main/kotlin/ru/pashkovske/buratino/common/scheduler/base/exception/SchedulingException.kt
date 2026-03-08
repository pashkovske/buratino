package ru.pashkovske.buratino.common.scheduler.base.exception

import java.util.UUID

class SchedulingException(
    message: String,
    taskId: UUID,
    cause: Throwable? = null
) : RuntimeException(
    "Error with scheduling $taskId: $message",
    cause
)
