package ru.pashkovske.buratino.assignment.repo

import ru.pashkovske.buratino.assignment.model.InstrumentAssignment
import java.util.UUID

interface AssignmentRepo<A : InstrumentAssignment> {
    fun getAll(): List<A>
    fun get(id: UUID): A
    fun create(assignment: A)
    fun update(assignment: A)
}