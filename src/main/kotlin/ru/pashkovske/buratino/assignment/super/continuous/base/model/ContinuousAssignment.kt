package ru.pashkovske.buratino.assignment.`super`.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class ContinuousAssignment<Nested: Assignment>(
    id: UUID,
    iid: InstrumentId,
    status: AssignmentState,
    refreshSchedulingProperties: SchedulingProperties?,
    nested: Nested,
    val continueSchedulingProperties: SchedulingProperties?
): SuperAssignment<Nested>(
    id = id,
    iid = iid,
    status = status,
    nested = nested,
    refreshSchedulingProperties = refreshSchedulingProperties
) {
    private var continueSchedulingInfo: SchedulingInfo? = null

    fun initContinueScheduling(schedulingInfo: SchedulingInfo) {
        if (continueSchedulingInfo != null) {
            throw IllegalStateException("Continue scheduling info is already initialized")
        }
        continueSchedulingInfo = schedulingInfo
    }

    fun clearContinueScheduling() {
        continueSchedulingInfo = null
    }

    fun getContinueSchedulingInfo(): SchedulingInfo? {
        return continueSchedulingInfo
    }
}
