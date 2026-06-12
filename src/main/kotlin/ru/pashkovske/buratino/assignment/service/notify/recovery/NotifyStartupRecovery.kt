package ru.pashkovske.buratino.assignment.service.notify.recovery

import jakarta.annotation.PostConstruct
import ru.pashkovske.buratino.assignment.dao.notify.NotifierDao
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifierSubscriber
import ru.pashkovske.buratino.assignment.model.notify.NotifierState
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentNotifier
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.common.scheduler.base.model.Tick
import java.time.Instant
import java.util.UUID
import kotlin.collections.forEach

abstract class NotifyStartupRecovery(
    private val notifierDao: NotifierDao,
    private val taskScheduler: TaskScheduler
) {

    @PostConstruct
    fun recoverNotifiers(): List<AssignmentNotifier> {
        val recoveredNotifiers: List<AssignmentNotifier> = notifierDao.findByState(NotifierState.ACTIVE)
        recoveredNotifiers
            .forEach { notifier: AssignmentNotifier ->
                val subscriber: AssignmentNotifierSubscriber = getSubscriber(notifier.assignmentId)
                val taskId: UUID = when (notifier) {
                    is PeriodicAssignmentNotifier -> startPeriodic(notifier, subscriber)
                }
                notifier.taskId = taskId
                notifierDao.update(notifier)
                subscriber.onNext(
                    Tick(
                        id = UUID.randomUUID(),
                        time = Instant.now()
                    )
                )
            }
        return recoveredNotifiers
    }

    protected abstract fun getSubscriber(assignmentId: UUID): AssignmentNotifierSubscriber

    private fun startPeriodic(
        notifier: PeriodicAssignmentNotifier,
        subscriber: AssignmentNotifierSubscriber
    ): UUID {
        val taskId: UUID = taskScheduler.startNewPeriodic(notifier.properties.period)
        taskScheduler.subscribePeriodic(taskId, subscriber)
        return taskId
    }
}