package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class ContinuousAssignmentDto<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ChildDto : BasicAssignmentDto<ChildA>
    >(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    refreshAssignmentScheduling: AssignmentSchedulingDto?,
    child: ChildDto,
    val continueAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    val continueAssignmentScheduling: AssignmentSchedulingDto?
) : ParentAssignmentDto<ContinuousA, ChildA, ChildDto>(
    id = id,
    iid = iid,
    state = state,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
    refreshAssignmentScheduling = refreshAssignmentScheduling,
    child = child
)