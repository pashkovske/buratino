package ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingState
import ru.pashkovske.buratino.assignment.limit.base.dao.postgre.LimitOrderAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
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
