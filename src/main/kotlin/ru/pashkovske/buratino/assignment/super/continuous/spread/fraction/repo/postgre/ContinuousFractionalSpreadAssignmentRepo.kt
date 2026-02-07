package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.repo.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.repo.postgre.PostgreAssignmentRepo
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.repo.postgre.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.`super`.base.repo.postgre.PostgreSuperAssignmentRepo
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Repository
class ContinuousFractionalSpreadAssignmentRepo(
    r2dbcRepo: ContinuousFractionalSpreadAssignmentRepoPostgre,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    continuousFractionalSpreadAssignmentMapper: ContinuousFractionalSpreadAssignmentMapper,
    fractionalSpreadAssignmentRepo: PostgreAssignmentRepo<FractionalSpreadAssignment, FractionalSpreadAssignmentRow>
): PostgreSuperAssignmentRepo<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignmentRow,
    ContinuousFractionalSpreadAssignmentRow
    >(
        mapper = continuousFractionalSpreadAssignmentMapper,
        r2dbcRepository = r2dbcRepo,
        r2dbcEntityTemplate = r2dbcEntityTemplate,
        nestedAssignmentRepo = fractionalSpreadAssignmentRepo
    )
