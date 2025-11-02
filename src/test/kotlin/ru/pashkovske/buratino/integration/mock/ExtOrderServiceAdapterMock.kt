package ru.pashkovske.buratino.integration.mock

import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderCommitResult
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.OrderState
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.money.model.Currency
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import java.time.Instant
import java.util.UUID

class ExtOrderServiceAdapterMock: ExtOrderServiceAdapter {
    private val orders = mutableMapOf<String, Order>()

    override fun createOrder(orderRequest: LimitOrderRequest): Order {
        val orderCommitResult = OrderCommitResult(
            commission = MoneyPrice(
                units = 0,
                nano = 0,
                currency = Currency.RUB
            ),
            time = Instant.now()
        )
        val currentInfo = OrderInstantInfo(
            remainingLots = orderRequest.lots,
            state = OrderState.ACTIVE,
            time = Instant.now()
        )
        val createdOrder = Order(
            id = UUID.randomUUID().toString(),
            iid = orderRequest.iid,
            request = orderRequest,
            commitResult = orderCommitResult,
            currentInfo = currentInfo
        )
        orders[createdOrder.id] = createdOrder
        return createdOrder
    }

    override fun replaceOrder(
        orderId: String,
        newOrderRequest: LimitOrderRequest
    ): Order {
        cancelOrder(orderId)
        return createOrder(newOrderRequest)
    }

    override fun getOrderActualInfo(orderId: String): OrderInstantInfo {
        return orders[orderId]?.currentInfo ?: throw IllegalArgumentException("Order not found")
    }

    override fun cancelOrder(orderId: String) {
        if (orders[orderId] == null || orders[orderId]?.currentInfo?.state == OrderState.COMPLETED) {
            throw IllegalArgumentException("Order not found")
        }
        orders[orderId]?.currentInfo = OrderInstantInfo(
            remainingLots = 0,
            state = OrderState.COMPLETED,
            time = Instant.now()
        )
    }
}
