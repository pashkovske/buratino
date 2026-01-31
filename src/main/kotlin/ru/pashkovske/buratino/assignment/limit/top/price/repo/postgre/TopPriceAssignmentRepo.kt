package ru.pashkovske.buratino.assignment.limit.top.price.repo.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.repo.postgre.PostgreAssignmentRepo
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment

@Repository
class TopPriceAssignmentRepo(
    r2dbcRepo: TopPriceAssignmentRepoPostgre,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    topPriceAssignmentMapper: TopPriceAssignmentMapper
): PostgreAssignmentRepo<
    TopPriceAssignment,
    TopPriceAssignmentRow
    >(
        mapper = topPriceAssignmentMapper,
        r2dbcRepository = r2dbcRepo,
        r2dbcEntityTemplate = r2dbcEntityTemplate
    )
