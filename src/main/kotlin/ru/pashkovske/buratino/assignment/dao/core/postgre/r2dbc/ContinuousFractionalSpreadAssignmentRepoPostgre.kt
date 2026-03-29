package ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ContinuousFractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment

interface ContinuousFractionalSpreadAssignmentRepoPostgre : AssignmentR2dbcRepo<
    ContinuousFractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentRow
    >