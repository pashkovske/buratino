package ru.pashkovske.buratino.assignment.base.model.scheduling

import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import java.util.UUID

class PeriodicAssignmentScheduling(
    id: UUID,
    assignmentId: UUID,
    taskId: UUID?,
    state: SchedulingState,
    override val properties: PeriodicAssignmentSchedulingProperties
) : AssignmentScheduling(
    id = id,
    assignmentId = assignmentId,
    taskId = taskId,
    state = state,
    properties = properties,
)
