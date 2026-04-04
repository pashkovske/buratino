package ru.pashkovske.buratino.assignment.service.core.refresh.dispatcher

import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import java.util.UUID

interface RefreshDispatcher {

    fun getRefresher(assignmentId: UUID): AssignmentRefresher<*>
}
