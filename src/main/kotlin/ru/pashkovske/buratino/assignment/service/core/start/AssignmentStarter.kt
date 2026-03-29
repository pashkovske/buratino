package ru.pashkovske.buratino.assignment.service.core.start

import ru.pashkovske.buratino.assignment.model.core.Assignment

interface AssignmentStarter<A : Assignment> {

    fun start(assignment: A): A
}
