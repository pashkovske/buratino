package ru.pashkovske.buratino.assignment.service

import ru.pashkovske.buratino.assignment.model.InstrumentAssignment
import java.util.UUID

interface AssignmentExecutor<T: InstrumentAssignment> {
    fun start(assignment: T)
    fun refresh(id: UUID)
    fun cancel(id: UUID)
}