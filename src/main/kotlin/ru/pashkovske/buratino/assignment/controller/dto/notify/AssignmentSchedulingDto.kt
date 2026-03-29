package ru.pashkovske.buratino.assignment.controller.dto.notify

import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import java.util.UUID

data class AssignmentSchedulingDto(
    val id: UUID,
    val properties: AssignmentSchedulingPropertiesDto?,
    val taskId: UUID?,
    val state: SchedulingState
)
