package ru.pashkovske.buratino.order.adapter

import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import java.time.Duration

interface ExtOrderServiceAdapter {

    fun getRequestsDelay(): Duration

    fun createOrder(orderRequest: LimitOrderRequest): Order
    fun replaceOrder(
        orderId: String,
        newOrderRequest: LimitOrderRequest
    ): Order
    fun getOrderActualInfo(orderId: String): OrderInstantInfo
    fun cancelOrder(orderId: String)
}
