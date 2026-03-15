package ru.pashkovske.buratino.assignment.base.controller.dto.scheduling

import java.time.Duration

data class PeriodicAssignmentSchedulingPropertiesDto(
    val period: Duration
) : AssignmentSchedulingPropertiesDto(
    type = AssignmentSchedulingType.PERIODIC
)
