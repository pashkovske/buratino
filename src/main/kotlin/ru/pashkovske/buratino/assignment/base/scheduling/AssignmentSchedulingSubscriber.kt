package ru.pashkovske.buratino.assignment.base.scheduling

import mu.KLogger
import mu.KotlinLogging
import org.reactivestreams.Subscription
import ru.pashkovske.buratino.common.scheduler.base.model.DisposableSubscriber
import ru.pashkovske.buratino.common.scheduler.base.model.Tick
import java.util.UUID
import java.util.function.Consumer

class AssignmentSchedulingSubscriber(
    private val action: Consumer<UUID>,
    private val assignmentId: UUID
): DisposableSubscriber<Tick>, AutoCloseable {

    private val log: KLogger = KotlinLogging.logger {}
    private var subscription: Subscription? = null
    private var isDisposed: Boolean = false

    override fun onSubscribe(s: Subscription) {
        this.subscription = s
    }

    override fun onNext(t: Tick) {
        try {
            action.accept(assignmentId)
        } catch (e: Exception) {
            log.error(e) { "Assignment $assignmentId scheduling task failed" }
        }
    }

    override fun onError(t: Throwable) {
        log.error(t) { "Assignment $assignmentId scheduling publisher produced error" }
    }

    override fun onComplete() {
        log.info { "Assignment $assignmentId scheduling publisher completed" }
    }

    override fun close() {
        dispose()
    }

    override fun dispose() {
        this.subscription?.cancel()
        isDisposed = true
    }

    override fun isDisposed(): Boolean = isDisposed
}
