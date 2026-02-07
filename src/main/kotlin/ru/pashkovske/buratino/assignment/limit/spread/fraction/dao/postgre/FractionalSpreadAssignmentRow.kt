package ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.assignment.limit.base.dao.postgre.LimitOrderAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.order.model.OrderDirection
import java.time.Duration
import java.util.UUID

@Table("assignment.fractional_spread")
data class FractionalSpreadAssignmentRow(
    @Id override val id: UUID,
    override val instrumentId: String,
    override val status: AssignmentStatus,
    override val refreshSchedulingPeriod: Duration?,
    override val refreshSchedulingTaskId: UUID?,
    override val refreshSchedulingStatus: SchedulingStatus?,
    override val orderDirection: OrderDirection,
    override val orderId: String?,
    val rate: Double
) : LimitOrderAssignmentPostgreRow<FractionalSpreadAssignment>(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingStatus = refreshSchedulingStatus,
    orderDirection = orderDirection,
    orderId = orderId
)
