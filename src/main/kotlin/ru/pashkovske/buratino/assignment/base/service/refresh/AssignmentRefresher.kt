package ru.pashkovske.buratino.assignment.base.service.refresh

import ru.pashkovske.buratino.assignment.base.model.Assignment
import java.util.UUID

interface AssignmentRefresher<A: Assignment> {

    fun refresh(id: UUID): A
}
