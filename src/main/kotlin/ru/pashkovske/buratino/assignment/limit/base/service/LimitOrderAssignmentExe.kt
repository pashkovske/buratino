package ru.pashkovske.buratino.assignment.limit.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.limit.base.model.LimitedOrderAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import java.util.UUID

private val logger = KotlinLogging.logger {}

abstract class LimitOrderAssignmentExe<LA : LimitedOrderAssignment>(
    val orderService: OrderService,
    override val assignmentRepo: AssignmentRepo<LA>
): BasicAssignmentExe<LA>(
    assignmentRepo = assignmentRepo
) {
    override fun doStart(assignment: LA) {
        val order: Order = orderService.createOrder(
            orderRequest = buildLimitReq(assignment)
        )
        assignment.info.orderId = order.id
    }

    override fun doRefresh(assignment: LA) {
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            logger.info("Order of assignment ${assignment.id} is already completed, skipping refresh")
            assignment.status = AssignmentStatus.COMPLETED
        }
        else {
            val newOrder: Order = orderService.replaceOrder(
                orderId = orderId,
                newOrderRequest = buildLimitReq(assignment)
            )
            logger.info("Refreshed order: $orderId")
            assignment.status = AssignmentStatus.IN_PROGRESS
            assignment.info.orderId = newOrder.id
        }
    }

    override fun doCancel(assignment: LA) {
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            logger.info("Order of assignment ${assignment.id} is already completed, skipping cancel")
        }
        else {
            orderService.cancelOrder(orderId)
            logger.info("Canceled order: $orderId")
        }
    }

    protected abstract fun getPrice(assignment: LA): MoneyPrice

    private fun buildLimitReq(assignment: LA): LimitOrderRequest {
        return LimitOrderRequest(
            iid = assignment.iid,
            direction = assignment.direction,
            lots = 1,
            idempotencyToken = UUID.randomUUID(),
            price = getPrice(assignment)
        )
    }

    private fun getOrderId(assignment: LA): String {
        return assignment.info.orderId
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }
}
