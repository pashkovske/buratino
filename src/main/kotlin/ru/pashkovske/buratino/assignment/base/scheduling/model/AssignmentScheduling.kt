package ru.pashkovske.buratino.assignment.base.scheduling.model

import java.util.UUID

data class AssignmentScheduling(
    val properties: SchedulingProperties?,
    var taskId: UUID,
    var status: SchedulingState = SchedulingState.QUEUED
)
