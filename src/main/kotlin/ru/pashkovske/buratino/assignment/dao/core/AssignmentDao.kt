package ru.pashkovske.buratino.assignment.dao.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.core.Assignment
import java.util.UUID

interface AssignmentDao<A : Assignment> {
    fun getAll(): List<A>
    fun getByState(state: AssignmentState): List<A>
    fun get(id: UUID): A
    fun find(id: UUID): A?
    fun create(assignment: A)
    fun update(assignment: A)
    fun deleteAll()
}