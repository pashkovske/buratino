package ru.pashkovske.buratino.assignment.service

import ru.pashkovske.buratino.assignment.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.model.LimitedOrderAssignment
import ru.pashkovske.buratino.assignment.repo.AssignmentRepo
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import java.util.UUID

private val logger = mu.KotlinLogging.logger {}

abstract class LimitOrderAssignmentExecutor<T : LimitedOrderAssignment>(
    val orderService: OrderService,
    val assignmentRepo: AssignmentRepo<T>
): AssignmentExecutor<T> {
    override fun start(assignment: T): T {
        logger.info("Starting assignment: $assignment")
        val order: Order = orderService.createOrder(
            orderRequest = buildLimitReq(assignment)
        )
        assignment.info.orderId = order.id
        assignment.status = AssignmentStatus.IN_PROGRESS
        assignmentRepo.create(assignment)
        return assignment
    }

    override fun refresh(id: UUID): T {
        logger.info("Refreshing assignment: $id")
        val assignment = assignmentRepo.get(id)
        if (assignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.id} is already completed, skipping refresh")
            return assignment
        }
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
        assignmentRepo.update(assignment)
        return assignment
    }

    override fun cancel(id: UUID): T {
        logger.info("Cancelling assignment: $id")
        val assignment = assignmentRepo.get(id)
        if (assignment.status == AssignmentStatus.COMPLETED) {
            logger.info("Assignment ${assignment.id} is already completed, skipping cancel")
            return assignment
        }
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            logger.info("Order of assignment ${assignment.id} is already completed, skipping cancel")
        }
        else {
            orderService.cancelOrder(orderId)
            logger.info("Canceled order: $orderId")
        }
        assignment.status = AssignmentStatus.COMPLETED
        assignmentRepo.update(assignment)
        return assignment
    }

    protected abstract fun getPrice(assignment: T): MoneyPrice

    private fun buildLimitReq(assignment: T): LimitOrderRequest {
        return LimitOrderRequest(
            iid = assignment.iid,
            direction = assignment.direction,
            lots = 1,
            idempotencyToken = UUID.randomUUID(),
            price = getPrice(assignment)
        )
    }

    private fun getOrderId(assignment: T): String {
        return assignment.info.orderId
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }
}
