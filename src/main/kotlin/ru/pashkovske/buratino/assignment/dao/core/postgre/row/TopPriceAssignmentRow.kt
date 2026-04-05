package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.order.model.OrderDirection
import java.util.UUID

@Table("assignment.top_price")
data class TopPriceAssignmentRow(
    @Id override val id: UUID,
    override val instrumentId: String,
    override val state: AssignmentState,
    override val refreshNotifierId: UUID?,
    override val orderDirection: OrderDirection,
    override val orderId: String?,
    val oneStepOver: Boolean
) : LimitOrderAssignmentPostgreRow<TopPriceAssignment>(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshNotifierId = refreshNotifierId,
    orderDirection = orderDirection,
    orderId = orderId
)
