package ru.pashkovske.buratino.assignment.parent.base.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.BasicAssignmentDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
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
    refreshAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    refreshAssignmentScheduling: AssignmentSchedulingDto?,
    val child: ChildDto
) : BasicAssignmentDto<ParentA>(
    id = id,
    iid = iid,
    state = state,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
    refreshAssignmentScheduling = refreshAssignmentScheduling
)
