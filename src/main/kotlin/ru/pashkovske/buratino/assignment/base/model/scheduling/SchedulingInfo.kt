package ru.pashkovske.buratino.assignment.base.model.scheduling

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID

data class SchedulingInfo(
    val properties: SchedulingProperties,
    val taskId: UUID,
    val task: SchedulingAssignmentTask<out Assignment>,
    var status: SchedulingStatus = SchedulingStatus.QUEUED
)
