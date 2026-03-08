package ru.pashkovske.buratino.common.scheduler

import ru.pashkovske.buratino.common.scheduler.base.model.DisposableSubscriber
import ru.pashkovske.buratino.common.scheduler.base.model.Tick
import java.time.Duration
import java.util.UUID

interface TaskScheduler {

    fun getPeriodicScheduledTasks(): Set<UUID>
    fun subscribePeriodic(
        taskId: UUID,
        subscriber: DisposableSubscriber<Tick>
    )
    fun startNewPeriodic(
        period: Duration,
        subscriber: DisposableSubscriber<Tick>
    ) : UUID
    fun stopPeriodic(taskId: UUID)
}
