package ru.pashkovske.buratino.assignment.dao.core.postgre.row

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import java.time.Duration
import java.util.UUID

@Table("assignment.continuous_fractional_spread")
data class ContinuousFractionalSpreadAssignmentRow(
    @Id override val id: UUID,
    override val instrumentId: String,
    override val state: AssignmentState,
    override val refreshSchedulingPeriod: Duration?,
    override val refreshSchedulingTaskId: UUID?,
    override val refreshSchedulingState: SchedulingState?,
    override val refreshSchedulingId: UUID?,
    override val childAssignmentId: UUID,
    override val continueSchedulingPeriod: Duration?,
    override val continueSchedulingTaskId: UUID?,
    override val continueSchedulingState: SchedulingState?,
    override val continueSchedulingId: UUID?
) : ContinuousAssignmentPostgreRow<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment
    >(
    id = id,
    instrumentId = instrumentId,
    state = state,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingState = refreshSchedulingState,
    refreshSchedulingId = refreshSchedulingId,
    childAssignmentId = childAssignmentId,
    continueSchedulingPeriod = continueSchedulingPeriod,
    continueSchedulingTaskId = continueSchedulingTaskId,
    continueSchedulingState = continueSchedulingState,
    continueSchedulingId = continueSchedulingId
)