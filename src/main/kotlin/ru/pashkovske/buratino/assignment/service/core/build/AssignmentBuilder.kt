package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd

interface AssignmentBuilder<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    > {

    fun build(cmd: Cmd): A
}
