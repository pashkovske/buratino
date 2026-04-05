package ru.pashkovske.buratino.assignment.model.core

import ru.pashkovske.buratino.assignment.model.AssignmentState
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

sealed class Assignment(
    val id: UUID,
    val iid: InstrumentId,
    var state: AssignmentState
) {

    companion object {
        val initialState: AssignmentState = AssignmentState.QUEUED
        fun generateId(): UUID {
            return UUID.randomUUID()
        }
    }

    private var refreshNotifierId: UUID? = null
    private var refreshNotifierInitialized: Boolean = false

    fun getRefreshNotifierId(): UUID? {
        return refreshNotifierId
    }

    fun initRefreshNotifierId(refreshNotifierId: UUID?) {
        if (refreshNotifierInitialized) {
            throw IllegalStateException("Refresh notifier id is already initialized with ${this.refreshNotifierId}")
        }
        this.refreshNotifierId = refreshNotifierId
        refreshNotifierInitialized = true
    }

    override fun toString(): String {
        return """
            {
                id = $id,
                iid = $iid,
                state = $state
            }
        """.trimIndent()
    }
}