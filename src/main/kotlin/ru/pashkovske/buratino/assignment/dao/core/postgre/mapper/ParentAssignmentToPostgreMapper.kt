package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ParentAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment

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