package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentNotifierDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

class ContinuousFractionalSpreadAssignmentDto(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshNotifier: AssignmentNotifierDto?,
    child: FractionalSpreadAssignmentDto,
    continueNotifier: AssignmentNotifierDto?
) : ContinuousAssignmentDto<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentDto
    >(
    id = id,
    iid = iid,
    state = state,
    refreshNotifier = refreshNotifier,
    child = child,
    continueNotifier = continueNotifier
)