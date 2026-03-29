package ru.pashkovske.buratino.assignment.service.core.refresh

import ru.pashkovske.buratino.assignment.model.core.Assignment
import java.util.UUID

interface AssignmentRefresher<A: Assignment> {

    fun refresh(id: UUID): A
}
