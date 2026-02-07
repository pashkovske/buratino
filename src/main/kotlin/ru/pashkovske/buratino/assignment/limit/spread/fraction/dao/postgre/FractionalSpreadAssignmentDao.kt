package ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.dao.postgre.PostgreAssignmentDao
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment

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
