package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.limit.order.OrderInfoDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
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