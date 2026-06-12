package ru.pashkovske.buratino.assignment.dao.core.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.dao.core.postgre.mapper.RepeatableFractionalSpreadAssignmentMapper
import ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc.RepeatableFractionalSpreadAssignmentRepoPostgre
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.RepeatableFractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment

@Repository
class RepeatableFractionalSpreadAssignmentDao(
    r2dbcRepo: RepeatableFractionalSpreadAssignmentRepoPostgre,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    repeatableFractionalSpreadAssignmentMapper: RepeatableFractionalSpreadAssignmentMapper,
    fractionalSpreadAssignmentDao: PostgreAssignmentDao<FractionalSpreadAssignment, FractionalSpreadAssignmentRow>
): PostgreParentAssignmentDao<
    FractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignment,
    FractionalSpreadAssignmentRow,
    RepeatableFractionalSpreadAssignmentRow
    >(
    mapper = repeatableFractionalSpreadAssignmentMapper,
    r2dbcRepository = r2dbcRepo,
    r2dbcEntityTemplate = r2dbcEntityTemplate,
    childAssignmentDao = fractionalSpreadAssignmentDao
)
