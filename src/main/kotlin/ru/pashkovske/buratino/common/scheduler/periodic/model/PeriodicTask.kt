package ru.pashkovske.buratino.common.scheduler.periodic.model

import reactor.core.publisher.Flux
import ru.pashkovske.buratino.common.scheduler.base.exception.SchedulingException
import ru.pashkovske.buratino.common.scheduler.base.model.DisposableSubscriber
import ru.pashkovske.buratino.common.scheduler.base.model.SchedulingState
import ru.pashkovske.buratino.common.scheduler.base.model.Tick
import java.time.Duration
import java.time.Instant
import java.util.UUID

class PeriodicTask(
    val id: UUID,
    val period: Duration,
) : AutoCloseable {

    private val ticker: Flux<Tick>
    private var subscriber: DisposableSubscriber<Tick>? = null
    private var state: SchedulingState

    init {
        state = SchedulingState.QUEUED
        ticker = Flux.interval(period)
            .map { _: Long ->
                Tick(
                    id = UUID.randomUUID(),
                    time = Instant.now()
                )
            }
    }

    fun subscribe(subscriber: DisposableSubscriber<Tick>) {
        if (this.subscriber != null) {
            throw SchedulingException(
                message = "Only one subscriber is allowed",
                taskId = id
            )
        }
        if (state != SchedulingState.QUEUED) {
            throw SchedulingException(
                message = "Task is not in QUEUED state",
                taskId = id
            )
        }
        this.subscriber = subscriber
        ticker.subscribe(subscriber)
        state = SchedulingState.RUNNING
    }

    fun stop() {
        state = SchedulingState.COMPLETED

        if (subscriber == null) return
        if (subscriber!!.isDisposed) return
        subscriber!!.dispose()
    }

    override fun close() {
        stop()
    }
}
