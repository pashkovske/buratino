package ru.pashkovske.buratino.assignment.model.notify

import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicNotifierProperties
import java.util.UUID

class PeriodicAssignmentNotifier(
    id: UUID,
    assignmentId: UUID,
    taskId: UUID?,
    state: NotifierState,
    override val properties: PeriodicNotifierProperties
) : AssignmentNotifier(
    id = id,
    assignmentId = assignmentId,
    taskId = taskId,
    state = state,
    properties = properties,
)
