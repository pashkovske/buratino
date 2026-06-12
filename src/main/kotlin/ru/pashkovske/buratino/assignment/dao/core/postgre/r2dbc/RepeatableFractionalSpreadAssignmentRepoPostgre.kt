package ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.RepeatableFractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment

interface RepeatableFractionalSpreadAssignmentRepoPostgre : AssignmentR2dbcRepo<
    RepeatableFractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignmentRow
    >
