package ru.pashkovske.buratino.assignment.parent.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class ContinuousAssignment<ChildA: Assignment>(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    refreshSchedulingProperties: SchedulingProperties?,
    child: ChildA,
    val continueSchedulingProperties: SchedulingProperties?
): ParentAssignment<ChildA>(
    id = id,
    iid = iid,
    state = state,
    child = child,
    refreshSchedulingProperties = refreshSchedulingProperties
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
