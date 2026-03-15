package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model

import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
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
