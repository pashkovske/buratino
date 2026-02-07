package ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.repo.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.nested.continuous.base.repo.postgre.ContinuousAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentMapper : ContinuousAssignmentToPostgreMapper<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentRow
    >() {

    override fun map(
        superRow: ContinuousFractionalSpreadAssignmentRow,
        nestedAssignment: FractionalSpreadAssignment
    ): ContinuousFractionalSpreadAssignment {
        val refreshSchedulingProperties: SchedulingProperties? = mapRefreshSchedulingProperties(superRow)
        val continueSchedulingProperties: SchedulingProperties? = mapContinueSchedulingProperties(superRow)

        val assignment = ContinuousFractionalSpreadAssignment(
            id = superRow.id,
            iid = mapIid(superRow.instrumentId),
            status = superRow.status,
            refreshSchedulingProperties = refreshSchedulingProperties,
            nested = nestedAssignment,
            continueSchedulingProperties = continueSchedulingProperties
        )

        initRefreshSchedulingInfo(superRow, assignment, refreshSchedulingProperties)
        initContinueSchedulingInfo(superRow, assignment, continueSchedulingProperties)

        return assignment
    }

    override fun map(assignment: ContinuousFractionalSpreadAssignment): ContinuousFractionalSpreadAssignmentRow {
        val nested = assignment.nested
        
        return ContinuousFractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            status = assignment.status,
            refreshSchedulingPeriod = assignment.refreshSchedulingProperties?.interval,
            refreshSchedulingTaskId = assignment.getRefreshSchedulingInfo()?.taskId,
            refreshSchedulingStatus = assignment.getRefreshSchedulingInfo()?.status,
            nestedAssignmentId = nested.id,
            continueSchedulingPeriod = assignment.continueSchedulingProperties?.interval,
            continueSchedulingTaskId = assignment.getContinueSchedulingInfo()?.taskId,
            continueSchedulingStatus = assignment.getContinueSchedulingInfo()?.status
        )
    }
}
