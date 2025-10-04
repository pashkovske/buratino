package ru.pashkovske.buratino.order.service

import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest

interface OrderService {
    fun createOrder(orderRequest: LimitOrderRequest): Order
    fun replaceOrder(
        order: Order,
        newOrderRequest: LimitOrderRequest
    ): Order
    fun refreshOrder(order: Order)
    fun cancelOrder(order: Order)
}