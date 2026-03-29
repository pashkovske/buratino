package ru.pashkovske.buratino.assignment.controller.dto.start

import ru.pashkovske.buratino.assignment.controller.dto.BasicStartAssignmentDto
import java.time.Duration

class StartFractionalSpreadAssignmentDto(
    val rate: Double,
    val continueSchedulingPeriod: Duration?,
    refreshSchedulingPeriod: Duration?
) : BasicStartAssignmentDto(
    refreshSchedulingPeriod = refreshSchedulingPeriod
)