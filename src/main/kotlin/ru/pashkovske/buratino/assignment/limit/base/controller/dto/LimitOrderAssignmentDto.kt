package ru.pashkovske.buratino.assignment.limit.base.controller.dto

import ru.pashkovske.buratino.assignment.base.controller.dto.BasicAssignmentDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

abstract class LimitOrderAssignmentDto<A : LimitOrderAssignment>(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingPropertiesDto?,
    refreshAssignmentScheduling: AssignmentSchedulingDto?,
    val direction: OrderDirection,
    val info: OrderInfoDto
) : BasicAssignmentDto<A>(
    id = id,
    iid = iid,
    state = state,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
    refreshAssignmentScheduling = refreshAssignmentScheduling
)
