package ru.pashkovske.buratino.assignment.dao.core.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.dao.core.postgre.mapper.ContinuousFractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc.ContinuousFractionalSpreadAssignmentRepoPostgre
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ContinuousFractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

@Repository
class ContinuousFractionalSpreadAssignmentDao(
    r2dbcRepo: ContinuousFractionalSpreadAssignmentRepoPostgre,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    continuousFractionalSpreadAssignmentMapper: ContinuousFractionalSpreadAssignmentMapper,
    fractionalSpreadAssignmentDao: PostgreAssignmentDao<FractionalSpreadAssignment, FractionalSpreadAssignmentRow>
): PostgreParentAssignmentDao<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignmentRow,
    ContinuousFractionalSpreadAssignmentRow
    >(
        mapper = continuousFractionalSpreadAssignmentMapper,
        r2dbcRepository = r2dbcRepo,
        r2dbcEntityTemplate = r2dbcEntityTemplate,
        childAssignmentDao = fractionalSpreadAssignmentDao
    )