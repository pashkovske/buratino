package ru.pashkovske.buratino.assignment.base.service.start

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd

interface AssignmentStarter<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    > {

    fun start(cmd: Cmd): A
}
