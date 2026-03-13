package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

class ContinuousFractionalSpreadAssignment(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshSchedulingProperties: SchedulingProperties?,
    nested: FractionalSpreadAssignment,
    continueSchedulingProperties: SchedulingProperties?
): ContinuousAssignment<FractionalSpreadAssignment>(
    id = id,
    iid = iid,
    state = state,
    refreshSchedulingProperties = refreshSchedulingProperties,
    nested = nested,
    continueSchedulingProperties = continueSchedulingProperties
) {

    companion object {
        fun newAssignment(
            iid: InstrumentId,
            refreshSchedulingProperties: SchedulingProperties?,
            nested: FractionalSpreadAssignment,
            continueSchedulingProperties: SchedulingProperties?
        ): ContinuousFractionalSpreadAssignment {
            return ContinuousFractionalSpreadAssignment(
                id = generateId(),
                iid = iid,
                state = initialState,
                refreshSchedulingProperties = refreshSchedulingProperties,
                nested = nested,
                continueSchedulingProperties = continueSchedulingProperties
            )
        }
    }
}
