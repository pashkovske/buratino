package ru.pashkovske.buratino.order.service

import jakarta.annotation.PostConstruct
import mu.KLogger
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.OrderState
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.dao.OrderDao
import ru.pashkovske.buratino.order.exception.OrderApiNotFoundException
import java.time.Instant

@Service
class OrderServiceImpl(
    private val orderDao: OrderDao,
    private val extOrderService: ExtOrderServiceAdapter
) : OrderService {

    private val log: KLogger = mu.KotlinLogging.logger {}

    @PostConstruct
    override fun refreshOrders(): List<Order> {
        val requestsDelay = extOrderService.getRequestsDelay()
        val activeOrders: List<Order> = orderDao.getByState(OrderState.ACTIVE)
            .collectList()
            .block() ?: throw IllegalStateException("Failed to get not completed orders")
        for (order: Order in activeOrders) {
            try {
                refreshOrder(order.id)
            } catch (e: Exception) {
                log.error(e) { "Failed to refresh order ${order.id} mark as completed" }
                order.currentInfo = order.currentInfo.copy(state = OrderState.COMPLETED)
                orderDao.update(order)
            }
            Thread.sleep(requestsDelay.toMillis())
        }
        return activeOrders
    }

    override fun createOrder(orderRequest: LimitOrderRequest): Order {
        log.info("Creating order with request\n$orderRequest")
        val order = extOrderService.createOrder(orderRequest)
        orderDao.create(order)
        return order
    }

    override fun replaceOrder(
        orderId: String,
        newOrderRequest: LimitOrderRequest
    ): Order {
        log.info("Replacing order $orderId with request\n$newOrderRequest")
        val order = orderDao.get(orderId)
        if (order.request == newOrderRequest) {
            log.info("Skipping replacing order $orderId - new order is identical")
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
        log.info("Refreshing order $orderId")
        val order = orderDao.get(orderId)
        if (order.currentInfo.state == OrderState.COMPLETED) {
            log.info("Order is already completed: $orderId, skipping refresh")
            return order
        }
        try {
            order.currentInfo = extOrderService.getOrderActualInfo(orderId)
        } catch (e: OrderApiNotFoundException) {
            log.error(e) { "Order not found while refreshing. Stubbing as completed" }
            order.currentInfo = OrderInstantInfo(
                state = OrderState.COMPLETED,
                remainingLots = 0,
                time = Instant.now()
            )
        }
        orderDao.update(order)
        return order
    }

    override fun cancelOrder(orderId: String): Order {
        log.info("Canceling order: $orderId")
        val order = orderDao.get(orderId)
        if (order.currentInfo.state == OrderState.COMPLETED) {
            log.info("Order is already completed: $orderId, skipping cancel")
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
