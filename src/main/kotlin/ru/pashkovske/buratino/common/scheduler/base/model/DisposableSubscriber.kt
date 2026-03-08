package ru.pashkovske.buratino.common.scheduler.base.model

import org.reactivestreams.Subscriber
import reactor.core.Disposable

interface DisposableSubscriber<T>: Subscriber<T>, Disposable
