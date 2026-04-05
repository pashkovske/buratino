package ru.pashkovske.buratino.assignment.controller.dto.notify

import java.time.Duration

data class PeriodicNotifierPropertiesDto(
    val period: Duration
) : NotifierPropertiesDto(
    type = AssignmentNotifierType.PERIODIC
)
