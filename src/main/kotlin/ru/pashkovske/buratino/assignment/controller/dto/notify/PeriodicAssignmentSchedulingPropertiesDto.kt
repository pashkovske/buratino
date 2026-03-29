package ru.pashkovske.buratino.assignment.controller.dto.notify

import java.time.Duration

data class PeriodicAssignmentSchedulingPropertiesDto(
    val period: Duration
) : AssignmentSchedulingPropertiesDto(
    type = AssignmentSchedulingType.PERIODIC
)
