package ru.pashkovske.buratino.assignment.dao.core.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.dao.core.postgre.mapper.TopPriceAssignmentMapper
import ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc.TopPriceAssignmentRepoPostgre
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.TopPriceAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment

@Repository
class TopPriceAssignmentDao(
    r2dbcRepo: TopPriceAssignmentRepoPostgre,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    topPriceAssignmentMapper: TopPriceAssignmentMapper
): PostgreAssignmentDao<
    TopPriceAssignment,
    TopPriceAssignmentRow
    >(
        mapper = topPriceAssignmentMapper,
        r2dbcRepository = r2dbcRepo,
        r2dbcEntityTemplate = r2dbcEntityTemplate
    )