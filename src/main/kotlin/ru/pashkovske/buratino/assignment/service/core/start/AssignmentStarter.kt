package ru.pashkovske.buratino.assignment.service.core.start

import ru.pashkovske.buratino.assignment.model.core.Assignment
import java.util.UUID

interface AssignmentStarter<A : Assignment> {

    fun start(id: UUID): A
}
