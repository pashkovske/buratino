package ru.pashkovske.buratino.assignment.service.notify.core

import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifierSubscriber
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
     * @param id notifier id
     * @param subscriber task executor. If notifier already running subscriber won't be subscribed
     * @return is task started
     */
    fun start(
        id: UUID?,
        subscriber: AssignmentNotifierSubscriber
    ): Boolean

    /**
     * If scheduling task registered and running, completes it.
     * - **Idempotent:** if task does not registered or already completed, does nothing
     * @return is task stopped
     * @throws ru.pashkovske.buratino.assignment.exception.AssignmentNotifyException if notifier is not registered
     */
    fun stop(id: UUID?) : Boolean

    fun shutdown()
}
