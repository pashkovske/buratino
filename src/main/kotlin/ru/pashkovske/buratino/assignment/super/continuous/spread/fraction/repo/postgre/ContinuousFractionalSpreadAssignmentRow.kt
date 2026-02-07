package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.repo.postgre

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingStatus
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.`super`.continuous.base.repo.postgre.ContinuousAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import java.time.Duration
import java.util.UUID

@Table("assignment.continuous_fractional_spread")
data class ContinuousFractionalSpreadAssignmentRow(
    @Id override val id: UUID,
    override val instrumentId: String,
    override val status: AssignmentStatus,
    override val refreshSchedulingPeriod: Duration?,
    override val refreshSchedulingTaskId: UUID?,
    override val refreshSchedulingStatus: SchedulingStatus?,
    override val nestedAssignmentId: UUID,
    override val continueSchedulingPeriod: Duration?,
    override val continueSchedulingTaskId: UUID?,
    override val continueSchedulingStatus: SchedulingStatus?
) : ContinuousAssignmentPostgreRow<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment
    >(
    id = id,
    instrumentId = instrumentId,
    status = status,
    refreshSchedulingPeriod = refreshSchedulingPeriod,
    refreshSchedulingTaskId = refreshSchedulingTaskId,
    refreshSchedulingStatus = refreshSchedulingStatus,
    nestedAssignmentId = nestedAssignmentId,
    continueSchedulingPeriod = continueSchedulingPeriod,
    continueSchedulingTaskId = continueSchedulingTaskId,
    continueSchedulingStatus = continueSchedulingStatus
)
