package ru.pashkovske.buratino.assignment.service.notify

import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import java.util.UUID

interface NotifyOrchestrator {

    fun get(id: UUID?): AssignmentNotifier?

    fun build(
        properties: NotifierProperties?,
        assignmentId: UUID
    ): AssignmentNotifier?

    fun register(notifier: AssignmentNotifier?): UUID?

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
