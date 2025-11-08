package ru.pashkovske.buratino.assignment.limit.top.price.controller.dto

import java.time.Duration

data class StartFractionalSpredAssignmentDto(
    val rate: Double,
    val schedulingInterval: Duration?
)
