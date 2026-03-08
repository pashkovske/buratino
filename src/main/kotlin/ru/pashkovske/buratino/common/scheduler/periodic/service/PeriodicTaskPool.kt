package ru.pashkovske.buratino.common.scheduler.periodic.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.common.scheduler.base.exception.SchedulingException
import ru.pashkovske.buratino.common.scheduler.base.model.DisposableSubscriber
import ru.pashkovske.buratino.common.scheduler.base.model.Tick
import ru.pashkovske.buratino.common.scheduler.periodic.model.PeriodicTask
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class PeriodicTaskPool {

    private final val periodicTasks: MutableMap<UUID, PeriodicTask> = ConcurrentHashMap()

    fun getPeriodicTasks(): Set<UUID> = periodicTasks.keys

    fun create(
        id: UUID,
        period: Duration
    ) {
        var isNew= false
        periodicTasks.computeIfAbsent(id) {
            isNew = true
            return@computeIfAbsent PeriodicTask(
                id = id,
                period = period
            )
        }
        if (!isNew) {
            throw SchedulingException(
                message = "Periodic task already exists",
                taskId = id
            )
        }
    }

    fun subscribe(
        id: UUID,
        subscriber: DisposableSubscriber<Tick>
    ) {
        val isSubscribed: Boolean = periodicTasks.computeIfPresent(id) { _, task: PeriodicTask ->
            task.subscribe(subscriber)
            return@computeIfPresent task
        } != null
        if (!isSubscribed) {
            throw SchedulingException(
                message = "Periodic task not found, cannot subscribe",
                taskId = id
            )
        }
    }

    fun stop(id: UUID) {
        var isStopped = false
        periodicTasks.computeIfPresent(id) { _, task: PeriodicTask ->
            task.stop()
            isStopped = true
            return@computeIfPresent null
        }
        if (!isStopped) {
            throw SchedulingException(
                message = "Periodic task not found, cannot stop",
                taskId = id
            )
        }
    }
}