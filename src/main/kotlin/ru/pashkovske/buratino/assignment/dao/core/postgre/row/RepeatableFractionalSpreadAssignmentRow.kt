package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import java.util.UUID

@Table("assignment.repeatable_fractional_spread")
data class RepeatableFractionalSpreadAssignmentRow(
    @Id override val id: UUID,
    override val instrumentId: String,
    override val state: AssignmentState,
    override val refreshNotifierId: UUID?,
    override val childAssignmentId: UUID
) : RepeatableAssignmentPostgreRow<
    FractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignment
    >(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshNotifierId = refreshNotifierId,
    childAssignmentId = childAssignmentId
)
