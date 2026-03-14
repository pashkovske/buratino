package ru.pashkovske.buratino.assignment.parent.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.dao.postgre.BasicAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment

abstract class ParentAssignmentToPostgreMapper<
    ChildA : Assignment,
    ParentA : ParentAssignment<ChildA>,
    ParentRow : ParentAssignmentPostgreRow<ParentA>
    > : BasicAssignmentToPostgreMapper<ParentA, ParentRow>() {

    abstract fun map(
        parentRow: ParentRow,
        childAssignment: ChildA
    ): ParentA

    final override fun map(row: ParentRow): Nothing {
        throw UnsupportedOperationException("Parent assignment cannot be mapped without child one.\n" +
            "Call with assignment id: ${row.id}")
    }
}
