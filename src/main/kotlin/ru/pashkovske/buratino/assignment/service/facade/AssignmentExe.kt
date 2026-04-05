package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import java.util.UUID

interface AssignmentExe<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
> {

    fun start(cmd: Cmd): A
    fun refresh(id: UUID): A
    fun cancel(id: UUID): A
}
