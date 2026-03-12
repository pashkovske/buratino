package ru.pashkovske.buratino.assignment.base.scheduling.model

import java.util.UUID

data class SchedulingInfo(
    val properties: SchedulingProperties,
    val taskId: UUID,
    var status: SchedulingState = SchedulingState.QUEUED
)
