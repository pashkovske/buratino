package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.dto.FractionalSpreadAssignmentDto
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.controller.dto.ContinuousAssignmentDto
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

class ContinuousFractionalSpreadAssignmentDto(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    refreshAssignmentScheduling: AssignmentSchedulingDto?,
    child: FractionalSpreadAssignmentDto,
    continueAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    continueAssignmentScheduling: AssignmentSchedulingDto?
) : ContinuousAssignmentDto<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentDto
    >(
    id = id,
    iid = iid,
    state = state,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
    refreshAssignmentScheduling = refreshAssignmentScheduling,
    child = child,
    continueAssignmentSchedulingProperties = continueAssignmentSchedulingProperties,
    continueAssignmentScheduling = continueAssignmentScheduling
)
