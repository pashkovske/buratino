package ru.pashkovske.buratino.assignment.base.service.notify

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import java.util.UUID

interface NotifierOrchestrator<A : Assignment> {

    fun build(
        properties: AssignmentSchedulingProperties?,
        assignmentId: UUID
    ): AssignmentScheduling?

    fun register(notifier: AssignmentScheduling?)

    /**
     * If scheduling task registered and not running, starts it.
     * - **Idempotent:** if task does not registered or already running, does nothing
     * @return is task started
     */
    fun start(id: UUID): Boolean
    /**
     * Starts all registered scheduling tasks for assignment.
     * - **Idempotent:** for already running tasks, does nothing
     * @return list of started tasks
     */
    fun startForAssignment(assignmentId: UUID): List<UUID>

    /**
     * If scheduling task registered and running, completes it.
     * - **Idempotent:** if task does not registered or already completed, does nothing
     * @return is task stopped
     */
    fun stop(id: UUID) : Boolean
    /**
     * Stops all registered scheduling tasks for assignment.
     * - **Idempotent:** for already completed tasks, does nothing
     * @return list of stopped tasks
     */
    fun stopForAssignment(assignmentId: UUID): List<UUID>

    fun shutdown()
}
