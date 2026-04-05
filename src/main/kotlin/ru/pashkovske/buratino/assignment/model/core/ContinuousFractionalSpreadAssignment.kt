package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

class ContinuousFractionalSpreadAssignment(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    child: FractionalSpreadAssignment
) : ContinuousAssignment<FractionalSpreadAssignment>(
    id = id,
    iid = iid,
    state = state,
    child = child
) {

    companion object {
        fun newAssignment(
            iid: InstrumentId,
            child: FractionalSpreadAssignment
        ): ContinuousFractionalSpreadAssignment {
            return ContinuousFractionalSpreadAssignment(
                id = generateId(),
                iid = iid,
                state = initialState,
                child = child
            )
        }
    }
}