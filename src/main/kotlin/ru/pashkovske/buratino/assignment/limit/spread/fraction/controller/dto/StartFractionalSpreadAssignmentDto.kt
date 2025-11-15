package ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.BasicStartAssignmentDto
import java.time.Duration

class StartFractionalSpreadAssignmentDto(
    val rate: Double,
    val continueSchedulingInterval: Duration?,
    refreshSchedulingInterval: Duration?
) : BasicStartAssignmentDto(
    refreshSchedulingInterval = refreshSchedulingInterval
)
