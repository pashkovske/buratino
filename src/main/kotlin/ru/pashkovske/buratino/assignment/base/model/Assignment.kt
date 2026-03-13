package ru.pashkovske.buratino.assignment.base.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class Assignment(
    val id: UUID,
    val iid: InstrumentId,
    var status: AssignmentState,
    val refreshSchedulingProperties: SchedulingProperties?
) {

    companion object {
        val initialStatus: AssignmentState = AssignmentState.QUEUED
        fun generateId(): UUID {
            return UUID.randomUUID()
        }
    }

    private var refreshAssignmentScheduling: AssignmentScheduling? = null

    fun initRefreshScheduling(assignmentScheduling: AssignmentScheduling) {
        if (refreshAssignmentScheduling != null) {
            throw IllegalStateException("Refresh scheduling info is already initialized")
        }
        refreshAssignmentScheduling = assignmentScheduling
    }

    fun clearRefreshScheduling() {
        refreshAssignmentScheduling = null
    }

    fun getRefreshSchedulingInfo(): AssignmentScheduling? {
        return refreshAssignmentScheduling
    }

    override fun toString(): String {
        return """
            {
                id = $id,
                iid = $iid,
                status = $status
            }
        """.trimIndent()
    }
}
