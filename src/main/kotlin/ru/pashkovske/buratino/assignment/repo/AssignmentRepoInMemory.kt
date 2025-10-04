package ru.pashkovske.buratino.assignment.repo

import ru.pashkovske.buratino.assignment.model.InstrumentAssignment
import java.util.UUID

abstract class AssignmentRepoInMemory<A : InstrumentAssignment> : AssignmentRepo<A> {
    private val assignments: MutableMap<UUID, A> = mutableMapOf()

    override fun getAll(): List<A> {
        return assignments.values.toList()
    }

    override fun get(id: UUID): A {
        if (id !in assignments) {
            throw IllegalArgumentException("Assignment with id $id is not found")
        }
        return assignments[id]!!
    }

    override fun create(assignment: A) {
        if (assignment.id in assignments) {
            throw IllegalArgumentException("Assignment with id ${assignment.id} already exists")
        }
        assignments[assignment.id] = assignment
    }

    override fun update(assignment: A) {
        if (assignment.id !in assignments) {
            throw IllegalArgumentException("Assignment with id ${assignment.id} is not found")
        }
        assignments[assignment.id] = assignment
    }
}