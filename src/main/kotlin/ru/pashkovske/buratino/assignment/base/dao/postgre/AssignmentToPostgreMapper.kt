package ru.pashkovske.buratino.assignment.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment

interface AssignmentToPostgreMapper<A : Assignment, Row : AssignmentPostgreRow<A>> {

    fun map (assignment: A): Row
    fun map (row: Row): A
}