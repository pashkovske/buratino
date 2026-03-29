package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.core.Assignment

interface AssignmentToPostgreMapper<A : Assignment, Row : AssignmentPostgreRow<A>> {

    fun map (assignment: A): Row
    fun map (row: Row): A
}