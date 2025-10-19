package ru.pashkovske.buratino.assignment.continuous.spread.fraction.model

import ru.pashkovske.buratino.assignment.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

data class ContinuousSpreadFractionAssignment(
    override val iid: InstrumentId,
    override var currentAssignment: FractionalSpreadAssignment
): ContinuousAssignment<FractionalSpreadAssignment>(
    iid = iid,
    currentAssignment = currentAssignment
)
