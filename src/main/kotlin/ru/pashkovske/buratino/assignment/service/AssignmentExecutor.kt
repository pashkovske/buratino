package ru.pashkovske.buratino.assignment.service

import ru.pashkovske.buratino.assignment.model.InstrumentAssignment

interface AssignmentExecutor<T: InstrumentAssignment> {
    fun run(assignment: T)
}