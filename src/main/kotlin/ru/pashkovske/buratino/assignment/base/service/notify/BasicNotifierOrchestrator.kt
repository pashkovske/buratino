package ru.pashkovske.buratino.assignment.base.service.notify

import ru.pashkovske.buratino.assignment.base.exception.scheduling.AssignmentNotifyException
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.base.model.scheduling.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class BasicNotifierOrchestrator<A : Assignment>(
    private val taskScheduler: TaskScheduler,
) : NotifierOrchestrator<A> {

    private data class AssignmentNotifierInstance(
        val notifier: AssignmentScheduling,
        val subscriber: AssignmentSchedulingSubscriber
    )

    private val notifiers: MutableMap<UUID, AssignmentScheduling> = ConcurrentHashMap()
    private val assignmentNotifiers: MutableMap<UUID, MutableList<UUID>> = ConcurrentHashMap()
    private val stateMachine: AssignmentNotifierStateMachine = AssignmentNotifierStateMachine

    override fun build(
        properties: AssignmentSchedulingProperties?,
        assignmentId: UUID
    ): AssignmentScheduling? {
        return when(properties) {
            null -> null
            is PeriodicAssignmentSchedulingProperties -> {
                PeriodicAssignmentScheduling(
                    id = UUID.randomUUID(),
                    assignmentId = assignmentId,
                    properties = properties,
                    taskId = null,
                    state = stateMachine.initialState
                )
            }
        }
    }

    abstract fun getSubscriber(assignmentId: UUID): AssignmentSchedulingSubscriber

    override fun register(notifier: AssignmentScheduling?) {
        if (notifier == null) {
            return
        }
        val isNew: Boolean = notifiers.putIfAbsent(notifier.id, notifier) == null
        if (!isNew) {
            throw AssignmentNotifyException(
                message = "Notifier is already registered",
                notifierId = notifier.id,
                assignmentId = notifier.assignmentId
            )
        }
        assignmentNotifiers.putIfAbsent(notifier.assignmentId, mutableListOf())
        assignmentNotifiers.computeIfPresent(notifier.assignmentId) { _, notifiers: MutableList<UUID> ->
            notifiers.add(notifier.id)
            notifiers
        }
    }

    override fun start(id: UUID): Boolean {
        val notifier: AssignmentScheduling = notifiers[id] ?: throw AssignmentNotifyException(
            message = "Notifier is not registered",
            notifierId = id,
            assignmentId = null
        )
        val subscriber: AssignmentSchedulingSubscriber = getSubscriber(notifier.assignmentId)
        val taskId: UUID = when(notifier) {
            is PeriodicAssignmentScheduling -> startPeriodic(notifier, subscriber)
        }
        notifier.taskId = taskId
        stateMachine.toActive(notifier)
        return true
    }

    private fun startPeriodic(
        notifier: PeriodicAssignmentScheduling,
        subscriber: AssignmentSchedulingSubscriber
    ): UUID {
        val taskId: UUID = taskScheduler.startNewPeriodic(notifier.properties.period)
        taskScheduler.subscribePeriodic(taskId, subscriber)
        return taskId
    }

    override fun startForAssignment(assignmentId: UUID): List<UUID> {
        return assignmentNotifiers[assignmentId]
            ?.map { notifierId: UUID ->
                start(notifierId)
                notifierId
            }
            ?: emptyList()
    }

    /**
     * If scheduling task registered and running, completes it.
     * - **Idempotent:** if task does not registered or already completed, does nothing
     * @return is task stopped
     */
    override fun stop(id: UUID): Boolean {
        val notifier: AssignmentScheduling = notifiers[id] ?: return false
        if (!stateMachine.canComplete(notifier.state)) {
            return false
        }
        val taskId: UUID = notifier.taskId ?: return false
        when (notifier) {
            is PeriodicAssignmentScheduling -> taskScheduler.stopPeriodic(taskId)
        }
        stateMachine.toComplete(notifier)
        return true
    }

    /**
     * Stops all registered scheduling tasks for assignment.
     * - **Idempotent:** for already completed tasks, does nothing
     * @return list of stopped tasks
     */
    override fun stopForAssignment(assignmentId: UUID): List<UUID> {
        return assignmentNotifiers[assignmentId]
            ?.filter { notifierId: UUID -> stop(notifierId) }
            ?: emptyList()
    }

    override fun shutdown() {
        notifiers.keys.forEach { id: UUID -> stop(id) }
    }
}
