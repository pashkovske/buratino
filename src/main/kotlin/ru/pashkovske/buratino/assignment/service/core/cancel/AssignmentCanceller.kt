package ru.pashkovske.buratino.assignment.service.core.cancel

import ru.pashkovske.buratino.assignment.model.core.Assignment
import java.util.UUID

interface AssignmentCanceller<A: Assignment> {

    fun cancel(id: UUID): A
}
