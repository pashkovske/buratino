package ru.pashkovske.buratino.assignment.limit.spread.fraction.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.limit.base.controller.dto.LimitOrderAssignmentDto
import ru.pashkovske.buratino.assignment.limit.base.controller.dto.OrderInfoDto
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

class FractionalSpreadAssignmentDto(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    refreshAssignmentScheduling: AssignmentSchedulingDto?,
    direction: OrderDirection,
    info: OrderInfoDto,
    val rate: Double
) : LimitOrderAssignmentDto<FractionalSpreadAssignment>(
    id = id,
    iid = iid,
    state = state,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
    refreshAssignmentScheduling = refreshAssignmentScheduling,
    direction = direction,
    info = info
)
