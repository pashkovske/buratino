package ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingProperties
import ru.pashkovske.buratino.assignment.nested.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

class ContinuousFractionalSpreadAssignment(
    iid: InstrumentId,
    nested: FractionalSpreadAssignment,
    schedulingProperties: SchedulingProperties?
): ContinuousAssignment<FractionalSpreadAssignment>(
    iid = iid,
    nested = nested,
    continueSchedulingProperties = schedulingProperties
)
