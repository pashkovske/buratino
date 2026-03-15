package ru.pashkovske.buratino.assignment.base.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class BasicAssignmentDto<A : Assignment>(
    val id: UUID,
    val iid: InstrumentId,
    val state: AssignmentState,
    val refreshAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    val refreshAssignmentScheduling: AssignmentSchedulingDto?
)
