package ru.pashkovske.buratino.assignment.parent.continuous.base.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.BasicAssignmentDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.parent.base.controller.dto.ParentAssignmentDto
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
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
