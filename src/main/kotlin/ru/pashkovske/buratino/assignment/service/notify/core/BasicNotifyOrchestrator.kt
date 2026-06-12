package ru.pashkovske.buratino.assignment.service.notify.core

import ru.pashkovske.buratino.assignment.dao.notify.NotifierDao
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifierSubscriber
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicNotifierProperties
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import java.util.UUID

abstract class BasicNotifyOrchestrator(
    private val taskScheduler: TaskScheduler,
    private val notifierDao: NotifierDao
) : NotifyOrchestrator {

    private val stateMachine: AssignmentNotifierStateMachine = AssignmentNotifierStateMachine

    override fun get(id: UUID?): AssignmentNotifier? {
        if (id == null) return null
        return notifierDao.get(id)
    }

    override fun build(
        properties: NotifierProperties?,
        assignmentId: UUID
    ): AssignmentNotifier? {
        return when (properties) {
            null -> null
            is PeriodicNotifierProperties -> {
                PeriodicAssignmentNotifier(
                    id = UUID.randomUUID(),
                    assignmentId = assignmentId,
                    properties = properties,
                    taskId = null,
                    state = stateMachine.initialState
                )
            }
        }
    }

    override fun register(notifier: AssignmentNotifier?): UUID? {
        return when (notifier) {
            null -> null
            is PeriodicAssignmentNotifier -> {
                notifierDao.create(notifier)
                notifier.id
            }
        }
    }

    override fun start(
        id: UUID?,
        subscriber: AssignmentNotifierSubscriber
    ): Boolean {
        if (id == null) {
            return false
        }
        val notifier: AssignmentNotifier = notifierDao.get(id)
        val taskId: UUID = when (notifier) {
            is PeriodicAssignmentNotifier -> startPeriodic(notifier, subscriber)
        }
        notifier.taskId = taskId
        stateMachine.toActive(notifier)
        notifierDao.update(notifier)
        return true
    }

    private fun startPeriodic(
        notifier: PeriodicAssignmentNotifier,
        subscriber: AssignmentNotifierSubscriber
    ): UUID {
        val taskId: UUID = taskScheduler.startNewPeriodic(notifier.properties.period)
        taskScheduler.subscribePeriodic(taskId, subscriber)
        return taskId
    }

    override fun stop(id: UUID?): Boolean {
        if (id == null) {
            return false
        }
        val notifier: AssignmentNotifier = notifierDao.get(id)
        if (!stateMachine.canComplete(notifier.state)) {
            return false
        }
        val taskId: UUID = notifier.taskId ?: return false
        taskScheduler.stopPeriodic(taskId)
        stateMachine.toComplete(notifier)
        notifierDao.update(notifier)
        return true
    }

    override fun shutdown() {
    }
}
