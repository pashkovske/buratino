package ru.pashkovske.buratino.assignment.base.service.build

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd

interface AssignmentBuilder<
    A : Assignment,
    Cmd : AssignmentStartCmd<A>
    > {

    fun build(cmd: Cmd): A
}
