package ru.pashkovske.buratino.assignment.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID

interface AssignmentExe<T: Assignment> {
    fun start(assignment: T): T
    fun refresh(id: UUID): T
    fun cancel(id: UUID): T
}