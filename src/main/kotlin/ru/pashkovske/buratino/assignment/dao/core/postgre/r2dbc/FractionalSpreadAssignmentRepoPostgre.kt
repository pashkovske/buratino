package ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

interface FractionalSpreadAssignmentRepoPostgre : AssignmentR2dbcRepo<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentRow
    >