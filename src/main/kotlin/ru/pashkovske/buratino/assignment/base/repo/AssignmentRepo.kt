package ru.pashkovske.buratino.assignment.base.repo

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID

interface AssignmentRepo<A : Assignment> {
    fun getAll(): List<A>
    fun get(id: UUID): A
    fun create(assignment: A)
    fun update(assignment: A)
    fun deleteAll()
}