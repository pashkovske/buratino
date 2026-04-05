package ru.pashkovske.buratino.assignment.controller.dto.notify

import ru.pashkovske.buratino.assignment.model.notify.NotifierState
import java.util.UUID

data class AssignmentNotifierDto(
    val id: UUID,
    val properties: NotifierPropertiesDto?,
    val taskId: UUID?,
    val state: NotifierState
)
