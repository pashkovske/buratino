package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
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