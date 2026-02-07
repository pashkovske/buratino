package ru.pashkovske.buratino.order.repo

import ru.pashkovske.buratino.order.model.Order

interface OrderRepo {
    fun get(id: String): Order
    fun getAll(): List<Order>
    fun create(order: Order)
    fun update(order: Order)
    fun deleteAll()
}