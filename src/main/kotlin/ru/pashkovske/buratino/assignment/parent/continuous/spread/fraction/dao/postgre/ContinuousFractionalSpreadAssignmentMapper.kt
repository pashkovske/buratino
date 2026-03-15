package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.dao.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.dao.postgre.ContinuousAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

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
        
        return ContinuousFractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            refreshSchedulingPeriod = assignment.refreshAssignmentSchedulingProperties?.period,
            refreshSchedulingTaskId = assignment.getRefreshAssignmentScheduling()?.taskId,
            refreshSchedulingState = assignment.getRefreshAssignmentScheduling()?.state,
            childAssignmentId = child.id,
            continueSchedulingPeriod = assignment.continueAssignmentSchedulingProperties?.period,
            continueSchedulingTaskId = assignment.getContinueAssignmentScheduling()?.taskId,
            continueSchedulingState = assignment.getContinueAssignmentScheduling()?.state
        )
    }
}
