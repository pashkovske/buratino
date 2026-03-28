package ru.pashkovske.buratino.assignment.base.model.scheduling

import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import java.util.UUID

sealed class AssignmentScheduling(
    val id: UUID,
    val assignmentId: UUID,
    open val properties: AssignmentSchedulingProperties?,
    var taskId: UUID,
    var state: SchedulingState = SchedulingState.QUEUED
)
