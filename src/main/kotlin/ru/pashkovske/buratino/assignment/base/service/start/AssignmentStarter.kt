package ru.pashkovske.buratino.assignment.base.service.start

import ru.pashkovske.buratino.assignment.base.model.Assignment

interface AssignmentStarter<A : Assignment> {

    fun start(assignment: A): A
}
