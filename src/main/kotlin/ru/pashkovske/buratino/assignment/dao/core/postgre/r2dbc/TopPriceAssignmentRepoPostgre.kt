package ru.pashkovske.buratino.assignment.dao.core.postgre.r2dbc

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.TopPriceAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment

interface TopPriceAssignmentRepoPostgre : AssignmentR2dbcRepo<
    TopPriceAssignment,
    TopPriceAssignmentRow
    >