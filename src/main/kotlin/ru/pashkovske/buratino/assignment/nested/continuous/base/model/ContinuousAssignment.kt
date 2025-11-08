package ru.pashkovske.buratino.assignment.nested.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingProperties
import ru.pashkovske.buratino.assignment.nested.base.model.SuperAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ContinuousAssignment<A: Assignment>(
    iid: InstrumentId,
    nested: A,
    val continueSchedulingProperties: SchedulingProperties?
): SuperAssignment<A>(
    iid = iid,
    nested = nested
) {
    private var continueSchedulingInfo: SchedulingInfo? = null

    fun initContinueScheduling(schedulingInfo: SchedulingInfo) {
        if (continueSchedulingInfo != null) {
            throw IllegalStateException("Continue scheduling info is already initialized")
        }
        continueSchedulingInfo = schedulingInfo
    }

    fun getContinueSchedulingInfo(): SchedulingInfo? {
        return continueSchedulingInfo
    }
}
