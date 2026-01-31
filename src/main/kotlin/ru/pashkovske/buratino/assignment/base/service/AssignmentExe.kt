package ru.pashkovske.buratino.assignment.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID

interface AssignmentExe<A: Assignment> {

    fun start(assignment: A): A
    fun refresh(id: UUID): A
    fun cancel(id: UUID): A
}
