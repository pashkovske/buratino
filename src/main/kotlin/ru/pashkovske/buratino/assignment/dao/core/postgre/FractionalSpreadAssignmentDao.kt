package ru.pashkovske.buratino.assignment.dao.core.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.dao.core.postgre.mapper.FractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc.FractionalSpreadAssignmentRepoPostgre
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

@Repository
class FractionalSpreadAssignmentDao(
    r2dbcRepo: FractionalSpreadAssignmentRepoPostgre,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    fractionalSpreadAssignmentMapper: FractionalSpreadAssignmentMapper
): PostgreAssignmentDao<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentRow
    >(
        mapper = fractionalSpreadAssignmentMapper,
        r2dbcRepository = r2dbcRepo,
        r2dbcEntityTemplate = r2dbcEntityTemplate
    )