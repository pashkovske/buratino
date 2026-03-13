package ru.pashkovske.buratino.assignment.`super`.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentState
import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class ContinuousAssignment<Nested: Assignment>(
    id: UUID,
    iid: InstrumentId,
    status: AssignmentState,
    refreshSchedulingProperties: SchedulingProperties?,
    nested: Nested,
    val continueSchedulingProperties: SchedulingProperties?
): SuperAssignment<Nested>(
    id = id,
    iid = iid,
    status = status,
    nested = nested,
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

    fun getContinueSchedulingInfo(): AssignmentScheduling? {
        return continueAssignmentScheduling
    }
}
