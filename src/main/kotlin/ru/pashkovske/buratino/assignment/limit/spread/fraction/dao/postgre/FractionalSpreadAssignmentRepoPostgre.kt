package ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre

import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentR2dbcRepo
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment

interface FractionalSpreadAssignmentRepoPostgre : AssignmentR2dbcRepo<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentRow
    >
