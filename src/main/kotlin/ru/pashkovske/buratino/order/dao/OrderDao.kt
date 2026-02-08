package ru.pashkovske.buratino.order.dao

import reactor.core.publisher.Flux
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderState

interface OrderDao {
    fun get(id: String): Order
    fun getAll(): List<Order>
    fun getByState(state: OrderState): Flux<Order>
    fun create(order: Order)
    fun update(order: Order)
    fun deleteAll()
}
