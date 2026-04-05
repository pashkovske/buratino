package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import java.util.UUID

@Table("assignment.continuous_fractional_spread")
data class ContinuousFractionalSpreadAssignmentRow(
    @Id override val id: UUID,
    override val instrumentId: String,
    override val state: AssignmentState,
    override val refreshNotifierId: UUID?,
    override val childAssignmentId: UUID,
    override val continueNotifierId: UUID?
) : ContinuousAssignmentPostgreRow<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment
    >(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshNotifierId = refreshNotifierId,
    childAssignmentId = childAssignmentId,
    continueNotifierId = continueNotifierId
)
