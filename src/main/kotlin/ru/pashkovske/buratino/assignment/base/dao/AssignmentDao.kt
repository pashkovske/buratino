package ru.pashkovske.buratino.assignment.base.dao

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID

interface AssignmentDao<A : Assignment> {
    fun getAll(): List<A>
    fun get(id: UUID): A
    fun create(assignment: A)
    fun update(assignment: A)
    fun deleteAll()
}