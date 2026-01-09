package ru.pashkovske.buratino.assignment.base.service

import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.base.model.scheduling.SchedulingAssignmentTask
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Component
class AssignmentTaskScheduler(
    private val taskScheduler: TaskScheduler
) {
    private val scheduledTasks: MutableMap<UUID, ScheduledFuture<*>> = ConcurrentHashMap()

    fun getScheduled(): Set<UUID> {
        return scheduledTasks.keys
    }

    fun start(
        task: SchedulingAssignmentTask,
        taskId: UUID,
        interval: Duration
    ) {
        var isNew = false
        scheduledTasks.computeIfAbsent(taskId) {
            isNew = true
            taskScheduler.scheduleWithFixedDelay(
                task,
                Instant.now().plus(interval),
                interval
            )
        }
        if (!isNew) {
            throw IllegalStateException("Task with id $taskId already exists")
        }
    }

    fun stop(taskId: UUID) {
        var exists = false
        scheduledTasks.compute(taskId) { _: UUID, future: ScheduledFuture<*>? ->
            exists = future != null
            future?.cancel(false)
            null
        }
        if (!exists) {
            throw IllegalStateException("Task with id $taskId does not exist")
        }
    }
}
