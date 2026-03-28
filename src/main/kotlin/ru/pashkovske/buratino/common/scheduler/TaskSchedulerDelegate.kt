package ru.pashkovske.buratino.common.scheduler

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.common.scheduler.base.model.DisposableSubscriber
import ru.pashkovske.buratino.common.scheduler.base.model.Tick
import ru.pashkovske.buratino.common.scheduler.periodic.service.PeriodicTaskPool
import java.time.Duration
import java.util.UUID

@Service
class TaskSchedulerDelegate(
    private val periodicTaskPool: PeriodicTaskPool
): TaskScheduler {

    override fun getPeriodicScheduledTasks(): Set<UUID> {
        return periodicTaskPool.getPeriodicTasks()
    }

    override fun startNewPeriodic(period: Duration): UUID {
        val taskId = UUID.randomUUID()
        periodicTaskPool.create(
            id = taskId,
            period = period
        )
        return taskId
    }

    override fun subscribePeriodic(
        taskId: UUID,
        subscriber: DisposableSubscriber<Tick>
    ) {
        periodicTaskPool.subscribe(
            id = taskId,
            subscriber = subscriber
        )
    }

    override fun stopPeriodic(taskId: UUID) {
        periodicTaskPool.stop(taskId)
    }
}
