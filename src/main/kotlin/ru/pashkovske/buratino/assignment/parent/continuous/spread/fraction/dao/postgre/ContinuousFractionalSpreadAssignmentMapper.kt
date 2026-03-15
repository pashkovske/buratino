package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.dao.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.dao.postgre.ContinuousAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import java.time.Duration

@Service
class ContinuousFractionalSpreadAssignmentMapper : ContinuousAssignmentToPostgreMapper<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentRow
    >() {

    override fun map(
        parentRow: ContinuousFractionalSpreadAssignmentRow,
        childAssignment: FractionalSpreadAssignment
    ): ContinuousFractionalSpreadAssignment {
        val refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties? = mapRefreshSchedulingProperties(parentRow)
        val continueAssignmentSchedulingProperties: AssignmentSchedulingProperties? = mapContinueSchedulingProperties(parentRow)

        val assignment = ContinuousFractionalSpreadAssignment(
            id = parentRow.id,
            iid = mapIid(parentRow.instrumentId),
            state = parentRow.state,
            refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
            child = childAssignment,
            continueAssignmentSchedulingProperties = continueAssignmentSchedulingProperties
        )

        initRefreshSchedulingInfo(parentRow, assignment, refreshAssignmentSchedulingProperties)
        initContinueSchedulingInfo(parentRow, assignment, continueAssignmentSchedulingProperties)

        return assignment
    }

    override fun map(assignment: ContinuousFractionalSpreadAssignment): ContinuousFractionalSpreadAssignmentRow {
        val child = assignment.child
        val refreshPeriod: Duration? = when (assignment.refreshAssignmentSchedulingProperties) {
            null -> null
            is PeriodicAssignmentSchedulingProperties -> assignment.refreshAssignmentSchedulingProperties.period
        }
        val continuePeriod: Duration? = when (assignment.continueAssignmentSchedulingProperties) {
            null -> null
            is PeriodicAssignmentSchedulingProperties -> assignment.continueAssignmentSchedulingProperties.period
        }
        
        return ContinuousFractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            refreshSchedulingPeriod = refreshPeriod,
            refreshSchedulingTaskId = assignment.getRefreshAssignmentScheduling()?.taskId,
            refreshSchedulingState = assignment.getRefreshAssignmentScheduling()?.state,
            childAssignmentId = child.id,
            continueSchedulingPeriod = continuePeriod,
            continueSchedulingTaskId = assignment.getContinueAssignmentScheduling()?.taskId,
            continueSchedulingState = assignment.getContinueAssignmentScheduling()?.state
        )
    }
}
