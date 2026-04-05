package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ContinuousFractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

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
            val assignment = ContinuousFractionalSpreadAssignment(
                id = parentRow.id,
                iid = mapIid(parentRow.instrumentId),
                state = parentRow.state,
                child = childAssignment
            )
            assignment.initRefreshNotifierId(parentRow.refreshNotifierId)
            assignment.initContinueNotifierId(parentRow.continueNotifierId)
    
            return assignment
        }
    
        override fun map(assignment: ContinuousFractionalSpreadAssignment): ContinuousFractionalSpreadAssignmentRow {
            val child = assignment.child
    
            return ContinuousFractionalSpreadAssignmentRow(
                id = assignment.id,
                instrumentId = assignment.iid.id,
                state = assignment.state,
                refreshNotifierId = assignment.getRefreshNotifierId(),
                childAssignmentId = child.id,
                continueNotifierId = assignment.getContinueNotifierId()
            )
        }
}