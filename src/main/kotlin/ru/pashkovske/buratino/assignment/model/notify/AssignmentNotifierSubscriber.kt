package ru.pashkovske.buratino.assignment.model.notify

import mu.KLogger
import mu.KotlinLogging
import org.reactivestreams.Subscription
import ru.pashkovske.buratino.common.scheduler.base.model.DisposableSubscriber
import ru.pashkovske.buratino.common.scheduler.base.model.Tick
import java.util.UUID
import java.util.function.Consumer

class AssignmentNotifierSubscriber(
    private val action: Consumer<UUID>,
    private val assignmentId: UUID
): DisposableSubscriber<Tick>, AutoCloseable {

    private val log: KLogger = KotlinLogging.logger {}
    private var subscription: Subscription? = null
    private var isDisposed: Boolean = false

    override fun onSubscribe(s: Subscription) {
        this.subscription = s
        s.request(1)
    }

    override fun onNext(t: Tick) {
        try {
            action.accept(assignmentId)
        } catch (e: Exception) {
            log.error(e) { "Assignment $assignmentId notifier task failed" }
        } finally {
            subscription!!.request(1)
        }
    }

    override fun onError(t: Throwable) {
        log.error(t) { "Assignment $assignmentId notifier publisher produced error" }
    }

    override fun onComplete() {
        log.info { "Assignment $assignmentId notifier publisher completed" }
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
