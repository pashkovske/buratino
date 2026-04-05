package ru.pashkovske.buratino.assignment.model.notify

import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import java.util.UUID

sealed class AssignmentNotifier(
    val id: UUID,
    val assignmentId: UUID,
    open val properties: NotifierProperties?,
    var taskId: UUID?,
    var state: NotifierState = NotifierState.QUEUED
)
