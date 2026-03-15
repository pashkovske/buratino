package ru.pashkovske.buratino.assignment.parent.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class ContinuousAssignment<ChildA: Assignment>(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?,
    child: ChildA,
    val continueAssignmentSchedulingProperties: AssignmentSchedulingProperties?
): ParentAssignment<ChildA>(
    id = id,
    iid = iid,
    state = state,
    child = child,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties
) {
    private var continueAssignmentScheduling: AssignmentScheduling? = null

    fun initContinueScheduling(assignmentScheduling: AssignmentScheduling) {
        if (continueAssignmentScheduling != null) {
            throw IllegalStateException("Continue scheduling info is already initialized")
        }
        continueAssignmentScheduling = assignmentScheduling
    }

    fun clearContinueScheduling() {
        continueAssignmentScheduling = null
    }

    fun getContinueAssignmentScheduling(): AssignmentScheduling? {
        return continueAssignmentScheduling
    }
}
