package ru.pashkovske.buratino.assignment.dao.notify

import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.NotifierState
import java.util.UUID

interface NotifierDao {

    fun get(id: UUID): AssignmentNotifier
    fun find(id: UUID): AssignmentNotifier?
    fun findByAssignmentId(assignmentId: UUID): List<AssignmentNotifier>
    fun findByState(state: NotifierState): List<AssignmentNotifier>
    fun create(notifier: AssignmentNotifier)
    fun update(notifier: AssignmentNotifier)
    fun delete(id: UUID)
    fun deleteAll()
}
