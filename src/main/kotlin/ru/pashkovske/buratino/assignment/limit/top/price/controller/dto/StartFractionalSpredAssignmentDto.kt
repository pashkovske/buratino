package ru.pashkovske.buratino.assignment.limit.top.price.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.BasicStartAssignmentDto
import java.time.Duration

class StartFractionalSpredAssignmentDto(
    val rate: Double,
    val continueSchedulingInterval: Duration?,
    refreshSchedulingInterval: Duration?
) : BasicStartAssignmentDto(
    refreshSchedulingInterval = refreshSchedulingInterval
)
