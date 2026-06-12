package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.RepeatableFractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment

@Service
class RepeatableFractionalSpreadAssignmentMapper : RepeatableAssignmentToPostgreMapper<
    FractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignmentRow
    >() {

    override fun map(
        parentRow: RepeatableFractionalSpreadAssignmentRow,
        childAssignment: FractionalSpreadAssignment
    ): RepeatableFractionalSpreadAssignment {
        val assignment = RepeatableFractionalSpreadAssignment(
            id = parentRow.id,
            iid = mapIid(parentRow.instrumentId),
            state = parentRow.state,
            child = childAssignment
        )
        assignment.initRefreshNotifierId(parentRow.refreshNotifierId)

        return assignment
    }

    override fun map(assignment: RepeatableFractionalSpreadAssignment): RepeatableFractionalSpreadAssignmentRow {
        val child = assignment.child

        return RepeatableFractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            refreshNotifierId = assignment.getRefreshNotifierId(),
            childAssignmentId = child.id
        )
    }
}
