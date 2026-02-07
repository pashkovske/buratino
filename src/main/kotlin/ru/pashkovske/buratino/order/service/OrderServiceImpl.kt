package ru.pashkovske.buratino.order.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.OrderState
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.dao.OrderDao
import java.time.Instant

private val logger = mu.KotlinLogging.logger {}

@Service
class OrderServiceImpl(
    private val orderDao: OrderDao,
    private val extOrderService: ExtOrderServiceAdapter
) : OrderService {
    override fun createOrder(orderRequest: LimitOrderRequest): Order {
        logger.info("Creating order $orderRequest")
        val order = extOrderService.createOrder(orderRequest)
        orderDao.create(order)
        return order
    }

    override fun replaceOrder(
        orderId: String,
        newOrderRequest: LimitOrderRequest
    ): Order {
        logger.info("Replacing order $orderId with $newOrderRequest")
        val order = orderDao.get(orderId)
        if (order.request == newOrderRequest) {
            logger.info("Skipping replacing order $orderId - new order is identical")
            return order
        }
        val newOrder = extOrderService.replaceOrder(
            orderId = order.id,
            newOrderRequest = newOrderRequest
        )
        order.currentInfo = OrderInstantInfo(
            state = OrderState.COMPLETED,
            remainingLots = 0,
            time = Instant.now()
        )
        orderDao.update(order)
        orderDao.create(newOrder)
        return newOrder
    }

    override fun refreshOrder(orderId: String): Order {
        logger.info("Refreshing order $orderId")
        val order = orderDao.get(orderId)
        order.currentInfo = extOrderService.getOrderActualInfo(orderId)
        orderDao.update(order)
        return order
    }

    override fun cancelOrder(orderId: String): Order {
        logger.info("Canceling order: $orderId")
        val order = orderDao.get(orderId)
        if (order.currentInfo.state == OrderState.COMPLETED) {
            logger.info("Order is already completed: $orderId, skipping cancel")
            return order
        }
        extOrderService.cancelOrder(orderId)
        order.currentInfo = OrderInstantInfo(
            state = OrderState.COMPLETED,
            remainingLots = 0,
            time = Instant.now()
        )
        orderDao.update(order)
        return order
    }

    override fun isOrderCompleted(orderId: String): Boolean {
        refreshOrder(orderId)
        return orderDao.get(orderId).currentInfo.state == OrderState.COMPLETED
    }
}