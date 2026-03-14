package ru.pashkovske.buratino.assignment.base.service.cancel

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID

interface AssignmentCanceller<A: Assignment> {

    fun cancel(id: UUID): A
}
