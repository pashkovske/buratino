package ru.pashkovske.buratino.assignment.service.notify

import ru.pashkovske.buratino.assignment.dao.notify.NotifierDao
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.AssignmentSchedulingSubscriber
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class BasicNotifyOrchestrator(
    private val taskScheduler: TaskScheduler,
    private val notifierDao: NotifierDao
) : NotifyOrchestrator {

    private val stateMachine: AssignmentNotifierStateMachine = AssignmentNotifierStateMachine

    override fun get(id: UUID?): AssignmentScheduling? {
        if (id == null) return null
        return notifierDao.get(id)
    }

    override fun build(
        properties: AssignmentSchedulingProperties?,
        assignmentId: UUID
    ): AssignmentScheduling? {
        return when (properties) {
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

    override fun register(notifier: AssignmentScheduling?): UUID? {
        return when (notifier) {
            null -> null
            is PeriodicAssignmentScheduling -> {
                notifierDao.create(notifier)
                notifier.id
            }
        }
    }

    override fun start(id: UUID?): Boolean {
        if (id == null) {
            return false
        }
        val notifier: AssignmentScheduling = notifierDao.get(id)
        val subscriber: AssignmentSchedulingSubscriber = getSubscriber(notifier.assignmentId)
        val taskId: UUID = when (notifier) {
            is PeriodicAssignmentScheduling -> startPeriodic(notifier, subscriber)
        }
        notifier.taskId = taskId
        stateMachine.toActive(notifier)
        notifierDao.update(notifier)
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
        return notifierDao.findByAssignmentId(assignmentId)
            .map { notifier: AssignmentScheduling ->
                start(notifier.id)
                notifier.id
            }
    }

    override fun stop(id: UUID?): Boolean {
        if (id == null) {
            return false
        }
        val notifier: AssignmentScheduling = notifierDao.get(id)
        if (!stateMachine.canComplete(notifier.state)) {
            return false
        }
        val taskId: UUID = notifier.taskId ?: return false
        taskScheduler.stopPeriodic(taskId)
        stateMachine.toComplete(notifier)
        notifierDao.update(notifier)
        return true
    }

    override fun stopForAssignment(assignmentId: UUID): List<UUID> {
        return notifierDao.findByAssignmentId(assignmentId)
            .filter { notifier: AssignmentScheduling -> stop(notifier.id) }
            .map { it.id }
    }

    override fun shutdown() {
    }
}
