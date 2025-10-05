package ru.pashkovske.buratino.order.repo

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.order.model.Order

@Repository
class OrderRepoInMemory : OrderRepo {
    private val orders: MutableMap<String, Order> = mutableMapOf()

    override fun get(id: String): Order {
        if (id !in orders) {
            throw IllegalArgumentException("Order with id $id is not found")
        }
        return orders[id]!!
    }

    override fun getAll(): List<Order> {
        return orders.values.toList()
    }

    override fun create(order: Order) {
        if (order.id in orders) {
            throw IllegalArgumentException("Order with id ${order.id} already exists")
        }
        orders[order.id] = order
    }

    override fun update(order: Order) {
        if (order.id !in orders) {
            throw IllegalArgumentException("Order with id ${order.id} is not found")
        }
        orders[order.id] = order
    }
}