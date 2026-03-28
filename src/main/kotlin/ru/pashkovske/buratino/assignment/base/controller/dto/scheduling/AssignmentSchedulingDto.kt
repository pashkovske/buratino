package ru.pashkovske.buratino.assignment.base.controller.dto.scheduling

import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import java.util.UUID

data class AssignmentSchedulingDto(
    val id: UUID,
    val properties: AssignmentSchedulingPropertiesDto?,
    val taskId: UUID?,
    val state: SchedulingState
)
