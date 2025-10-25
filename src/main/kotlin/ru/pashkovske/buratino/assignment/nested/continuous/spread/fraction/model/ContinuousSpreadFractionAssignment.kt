package ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.model

import ru.pashkovske.buratino.assignment.nested.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

data class ContinuousSpreadFractionAssignment(
    override val iid: InstrumentId,
    override var nested: FractionalSpreadAssignment
): ContinuousAssignment<FractionalSpreadAssignment>(
    iid = iid,
    nested = nested
)
