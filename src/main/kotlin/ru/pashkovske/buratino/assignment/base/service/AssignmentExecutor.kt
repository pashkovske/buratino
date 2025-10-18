package ru.pashkovske.buratino.assignment.base.service

import ru.pashkovske.buratino.assignment.base.model.InstrumentAssignment
import java.util.UUID

interface AssignmentExecutor<T: InstrumentAssignment> {
    fun start(assignment: T): T
    fun refresh(id: UUID): T
    fun cancel(id: UUID): T
}