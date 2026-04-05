package ru.pashkovske.buratino.assignment.controller.dto.start

import ru.pashkovske.buratino.assignment.controller.dto.BasicStartAssignmentDto
import java.time.Duration

class StartFractionalSpreadAssignmentDto(
    val rate: Double,
    val continueNotifyPeriod: Duration?,
    refreshNotifyPeriod: Duration?
) : BasicStartAssignmentDto(
    refreshNotifyPeriod = refreshNotifyPeriod
)