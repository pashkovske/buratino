package ru.pashkovske.buratino.assignment.limit.spread.fraction.repo.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.repo.postgre.PostgreAssignmentRepo
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment

@Repository
class FractionalSpreadAssignmentRepo(
    r2dbcRepo: FractionalSpreadAssignmentRepoPostgre,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    fractionalSpreadAssignmentMapper: FractionalSpreadAssignmentMapper
): PostgreAssignmentRepo<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentRow
    >(
        mapper = fractionalSpreadAssignmentMapper,
        r2dbcRepository = r2dbcRepo,
        r2dbcEntityTemplate = r2dbcEntityTemplate
    )
