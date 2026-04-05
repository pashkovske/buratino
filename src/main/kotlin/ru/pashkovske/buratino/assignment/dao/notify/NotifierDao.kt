package ru.pashkovske.buratino.assignment.dao.notify

import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import java.util.UUID

interface NotifierDao {

    fun get(id: UUID): AssignmentScheduling
    fun find(id: UUID): AssignmentScheduling?
    fun findByAssignmentId(assignmentId: UUID): List<AssignmentScheduling>
    fun findByState(state: SchedulingState): List<AssignmentScheduling>
    fun create(notifier: AssignmentScheduling)
    fun update(notifier: AssignmentScheduling)
    fun delete(id: UUID)
    fun deleteAll()
}
