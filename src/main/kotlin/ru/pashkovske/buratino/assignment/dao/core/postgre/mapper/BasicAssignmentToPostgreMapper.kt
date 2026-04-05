package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class BasicAssignmentToPostgreMapper<
    A : Assignment,
    Row : AssignmentPostgreRow<A>
    > : AssignmentToPostgreMapper<A, Row> {

    protected fun mapIid(iid: String): InstrumentId {
        return InstrumentId(iid)
    }
}
