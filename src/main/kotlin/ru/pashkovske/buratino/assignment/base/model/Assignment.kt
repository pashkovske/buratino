package ru.pashkovske.buratino.assignment.base.model

import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class Assignment(
    val id: UUID = UUID.randomUUID(),
    val iid: InstrumentId,
    var status: AssignmentStatus = AssignmentStatus.QUEUED,
    val refreshSchedulingProperties: SchedulingProperties?
) {
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
}
