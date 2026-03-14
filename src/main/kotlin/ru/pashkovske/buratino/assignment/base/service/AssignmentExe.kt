package ru.pashkovske.buratino.assignment.base.service

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import java.util.UUID

interface AssignmentExe<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
> {

    fun recoverAssignments(): List<A>
    fun start(cmd: Cmd): A
    fun refresh(id: UUID): A
    fun cancel(id: UUID): A
}
