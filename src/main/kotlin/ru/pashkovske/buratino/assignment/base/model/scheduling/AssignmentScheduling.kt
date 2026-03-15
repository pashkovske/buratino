package ru.pashkovske.buratino.assignment.base.model.scheduling

import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import java.util.UUID

data class AssignmentScheduling(
    val properties: AssignmentSchedulingProperties?,
    var taskId: UUID,
    var state: SchedulingState = SchedulingState.QUEUED
)
