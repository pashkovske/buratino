package ru.pashkovske.buratino.assignment.`super`.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.repo.postgre.BasicAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment

abstract class SuperAssignmentToPostgreMapper<
    NestedA : Assignment,
    SuperA : SuperAssignment<NestedA>,
    SuperRow : SuperAssignmentPostgreRow<SuperA>
    > : BasicAssignmentToPostgreMapper<SuperA, SuperRow>() {

    abstract fun map(
        superRow: SuperRow,
        nestedAssignment: NestedA
    ): SuperA

    final override fun map(row: SuperRow): Nothing {
        throw UnsupportedOperationException("Super assignment cannot be mapped without nested one.\n" +
            "Call with assignment id: ${row.id}")
    }
}
