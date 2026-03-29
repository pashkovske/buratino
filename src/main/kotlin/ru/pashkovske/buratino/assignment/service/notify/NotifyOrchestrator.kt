package ru.pashkovske.buratino.assignment.service.notify

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import java.util.UUID

interface NotifyOrchestrator<A : Assignment> {

    fun get(id: UUID?): AssignmentScheduling?

    fun build(
        properties: AssignmentSchedulingProperties?,
        assignmentId: UUID
    ): AssignmentScheduling?

    fun register(notifier: AssignmentScheduling?): UUID?

    /**
     * If scheduling task registered and not running, starts it.
     * - **Idempotent:** if task does not registered or already running, does nothing
     * @return is task started
     */
    fun start(id: UUID?): Boolean
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
     * @throws ru.pashkovske.buratino.assignment.exception.AssignmentNotifyException if notifier is not registered
     */
    fun stop(id: UUID?) : Boolean
    /**
     * Stops all registered scheduling tasks for assignment.
     * - **Idempotent:** for already completed tasks, does nothing
     * @return list of stopped tasks
     */
    fun stopForAssignment(assignmentId: UUID): List<UUID>

    fun shutdown()
}
