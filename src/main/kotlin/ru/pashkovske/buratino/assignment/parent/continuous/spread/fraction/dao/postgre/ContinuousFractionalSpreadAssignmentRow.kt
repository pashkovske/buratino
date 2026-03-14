package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.dao.postgre

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingState
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.dao.postgre.ContinuousAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
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
    override val childAssignmentId: UUID,
    override val continueSchedulingPeriod: Duration?,
    override val continueSchedulingTaskId: UUID?,
    override val continueSchedulingState: SchedulingState?
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
    childAssignmentId = childAssignmentId,
    continueSchedulingPeriod = continueSchedulingPeriod,
    continueSchedulingTaskId = continueSchedulingTaskId,
    continueSchedulingState = continueSchedulingState
)
