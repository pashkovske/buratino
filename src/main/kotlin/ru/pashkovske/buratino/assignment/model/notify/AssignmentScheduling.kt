package ru.pashkovske.buratino.assignment.model.notify

import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import java.util.UUID

sealed class AssignmentScheduling(
    val id: UUID,
    val assignmentId: UUID,
    open val properties: AssignmentSchedulingProperties?,
    var taskId: UUID?,
    var state: SchedulingState = SchedulingState.QUEUED
)
