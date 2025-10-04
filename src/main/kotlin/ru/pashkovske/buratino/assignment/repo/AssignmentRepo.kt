package ru.pashkovske.buratino.assignment.repo

import ru.pashkovske.buratino.assignment.model.InstrumentAssignment

interface AssignmentRepo<A : InstrumentAssignment> {
    fun getAll(): List<A>
    fun create(assignment: A)
}