package ru.pashkovske.buratino.assignment.base.model

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class Assignment(
    val id: UUID,
    val iid: InstrumentId,
    var status: AssignmentStatus,
    val refreshSchedulingProperties: SchedulingProperties?
) {

    companion object {
        val initialStatus: AssignmentStatus = AssignmentStatus.QUEUED
        fun generateId(): UUID {
            return UUID.randomUUID()
        }
    }

    private var refreshSchedulingInfo: SchedulingInfo? = null

    fun initRefreshScheduling(schedulingInfo: SchedulingInfo) {
        if (refreshSchedulingInfo != null) {
            throw IllegalStateException("Refresh scheduling info is already initialized")
        }
        refreshSchedulingInfo = schedulingInfo
    }

    fun getRefreshSchedulingInfo(): SchedulingInfo? {
        return refreshSchedulingInfo
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
