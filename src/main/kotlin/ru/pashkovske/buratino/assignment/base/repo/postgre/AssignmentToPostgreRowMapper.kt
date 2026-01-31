package ru.pashkovske.buratino.assignment.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment

interface AssignmentToPostgreRowMapper<A : Assignment, Row> {

    fun map (assignment: A): Row
    fun map (row: Row): A
}