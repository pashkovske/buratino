package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import ru.pashkovske.buratino.order.model.OrderDirection
import java.time.Duration
import java.util.UUID

@Table("assignment.fractional_spread")
data class FractionalSpreadAssignmentRow(
    @Id override val id: UUID,
    override val instrumentId: String,
    override val state: AssignmentState,
    override val refreshSchedulingPeriod: Duration?,
    override val refreshSchedulingTaskId: UUID?,
    override val refreshSchedulingState: SchedulingState?,
    override val refreshSchedulingId: UUID?,
    override val orderDirection: OrderDirection,
    override val orderId: String?,
    val rate: Double
) : LimitOrderAssignmentPostgreRow<FractionalSpreadAssignment>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState,
    refreshSchedulingId = refreshSchedulingId,
    orderDirection = orderDirection,
    orderId = orderId
)