package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentNotifierDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class ParentAssignmentDto<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment,
    ChildDto : BasicAssignmentDto<ChildA>
    >(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshNotifier: AssignmentNotifierDto?,
    val child: ChildDto
) : BasicAssignmentDto<ParentA>(
    id = id,
    iid = iid,
    state = state,
    refreshNotifier = refreshNotifier
)