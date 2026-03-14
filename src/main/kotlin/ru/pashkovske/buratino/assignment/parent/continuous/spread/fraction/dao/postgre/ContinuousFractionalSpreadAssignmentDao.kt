package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.dao.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.base.dao.postgre.PostgreAssignmentDao
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.parent.base.dao.postgre.PostgreParentAssignmentDao
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

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
