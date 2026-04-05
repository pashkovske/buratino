package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentNotifierDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class BasicAssignmentDto<A : Assignment>(
    val id: UUID,
    val iid: InstrumentId,
    val state: AssignmentState,
    val refreshNotifier: AssignmentNotifierDto?
)
