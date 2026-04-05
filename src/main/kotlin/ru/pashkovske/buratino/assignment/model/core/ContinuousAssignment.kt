package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

sealed class ContinuousAssignment<ChildA : Assignment>(
    id: UUID,
    iid: InstrumentId,
    state: AssignmentState,
    child: ChildA
) : ParentAssignment<ChildA>(
    id = id,
    iid = iid,
    state = state,
    child = child
) {

    private var continueNotifierId: UUID? = null
    private var continueNotifierInitialized: Boolean = false

    fun getContinueNotifierId(): UUID? {
        return continueNotifierId
    }

    fun initContinueNotifierId(continueNotifierId: UUID?) {
        if (continueNotifierInitialized) {
            throw IllegalStateException("Continue notifier id is already initialized with $continueNotifierId")
        }
        this.continueNotifierId = continueNotifierId
        continueNotifierInitialized = true
    }
}