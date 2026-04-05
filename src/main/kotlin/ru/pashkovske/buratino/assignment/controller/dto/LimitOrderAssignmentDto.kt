package ru.pashkovske.buratino.assignment.controller.dto

import ru.pashkovske.buratino.assignment.controller.dto.limit.order.OrderInfoDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentNotifierDto
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

abstract class LimitOrderAssignmentDto<A : LimitOrderAssignment>(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshNotifier: AssignmentNotifierDto?,
    val direction: OrderDirection,
    val info: OrderInfoDto
) : BasicAssignmentDto<A>(
    id = id,
    iid = iid,
    state = state,
    refreshNotifier = refreshNotifier
)