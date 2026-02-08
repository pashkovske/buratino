package ru.pashkovske.buratino.order.dao

import reactor.core.publisher.Flux
import ru.pashkovske.buratino.order.model.Order

interface OrderDao {
    fun get(id: String): Order
    fun getAll(): List<Order>
    fun getAllNotCompleted(): Flux<Order>
    fun create(order: Order)
    fun update(order: Order)
    fun deleteAll()
}
