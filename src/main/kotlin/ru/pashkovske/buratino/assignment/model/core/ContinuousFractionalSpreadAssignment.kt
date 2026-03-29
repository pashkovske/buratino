package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

class ContinuousFractionalSpreadAssignment(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?,
    child: FractionalSpreadAssignment,
    continueAssignmentSchedulingProperties: AssignmentSchedulingProperties?
): ContinuousAssignment<FractionalSpreadAssignment>(
    id = id,
    iid = iid,
    state = state,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
    child = child,
    continueAssignmentSchedulingProperties = continueAssignmentSchedulingProperties
) {

    companion object {
        fun newAssignment(
            iid: InstrumentId,
            refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?,
            child: FractionalSpreadAssignment,
            continueAssignmentSchedulingProperties: AssignmentSchedulingProperties?
        ): ContinuousFractionalSpreadAssignment {
            return ContinuousFractionalSpreadAssignment(
                id = generateId(),
                iid = iid,
                state = initialState,
                refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
                child = child,
                continueAssignmentSchedulingProperties = continueAssignmentSchedulingProperties
            )
        }
    }
}