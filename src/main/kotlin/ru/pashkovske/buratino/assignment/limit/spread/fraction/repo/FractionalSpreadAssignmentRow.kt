package ru.pashkovske.buratino.assignment.limit.spread.fraction.repo

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.order.model.OrderDirection
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Table("fractional_spread_assignment_row")
data class FractionalSpreadAssignmentRow(
    @Id val id: UUID,
    val instrumentId: String,
    val status: AssignmentStatus,
    val orderDirection: OrderDirection,
    val rate: Double,
    val refreshSchedulingPeriod: Duration?,
    val refreshSchedulingTaskId: UUID?,
    val refreshSchedulingStatus: SchedulingStatus?,
    val orderId: String?,
    val lastOrderUpdate: Instant
)
