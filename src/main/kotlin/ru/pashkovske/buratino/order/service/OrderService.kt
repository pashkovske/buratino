package ru.pashkovske.buratino.order.service

import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest

interface OrderService {

    fun refreshOrders()
    fun createOrder(orderRequest: LimitOrderRequest): Order
    fun replaceOrder(
        orderId: String,
        newOrderRequest: LimitOrderRequest
    ): Order
    fun refreshOrder(orderId: String): Order
    fun cancelOrder(orderId: String): Order
    fun isOrderCompleted(orderId: String): Boolean
}