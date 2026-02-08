package ru.pashkovske.buratino.assignment.limit.top.price.dao.postgre

import ru.pashkovske.buratino.assignment.base.dao.postgre.AssignmentR2dbcRepo
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment

interface TopPriceAssignmentRepoPostgre : AssignmentR2dbcRepo<
    TopPriceAssignment,
    TopPriceAssignmentRow
    >
