package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentNotifierDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

class RepeatableFractionalSpreadAssignmentDto(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshNotifier: AssignmentNotifierDto?,
    child: FractionalSpreadAssignmentDto
) : RepeatableAssignmentDto<
    RepeatableFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentDto
    >(
    id = id,
    iid = iid,
    state = state,
    refreshNotifier = refreshNotifier,
    child = child
)
