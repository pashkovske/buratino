package ru.pashkovske.buratino.assignment.limit.base.service

import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentAction
import ru.pashkovske.buratino.assignment.base.model.action.AssignmentActionResult
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.limit.base.model.LimitedOrderAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.quotation.money.model.MoneyPrice
import java.util.UUID

private val logger = KotlinLogging.logger {}

abstract class LimitOrderAssignmentExe<LA : LimitedOrderAssignment>(
    private val orderService: OrderService,
    assignmentRepo: AssignmentRepo<LA>,
    assignmentScheduler: AssignmentTaskScheduler
): BasicAssignmentExe<LA>(
    assignmentRepo = assignmentRepo,
    assignmentScheduler = assignmentScheduler
) {
    init {
        addStartOrderToChain()
        addRefreshOrderToChain()
        addCancelOrderToChain()
    }

    private fun addStartOrderToChain() {
        startAssignmentChain["log_start"] = AssignmentAction(
            name = "start_limit_order",
            action = { assignment: LA ->
                val order: Order = orderService.createOrder(
                    orderRequest = buildLimitReq(assignment)
                )
                assignment.info.orderId = order.id
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }

    private fun buildLimitReq(assignment: LA): LimitOrderRequest {
        return LimitOrderRequest(
            iid = assignment.iid,
            direction = assignment.direction,
            lots = 1,
            idempotencyToken = UUID.randomUUID(),
            price = getPrice(assignment)
        )
    }

    private fun addRefreshOrderToChain() {
        refreshAssignmentChain["check_completed"] = AssignmentAction(
            name = "refresh_limit_order",
            action = { assignment: LA ->
                val orderId: String = getOrderId(assignment)
                if (orderService.isOrderCompleted(orderId)) {
                    logger.info("Order of assignment ${assignment.id} is already completed, skipping order refresh")
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
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }

    private fun addCancelOrderToChain() {
        cancelAssignmentChain["stop_scheduling_refresh"] = AssignmentAction(
            name = "cancel_limit_order",
            action = { assignment: LA ->
                val orderId: String = getOrderId(assignment)
                if (orderService.isOrderCompleted(orderId)) {
                    logger.info("Order of assignment ${assignment.id} is already completed, skipping order cancel")
                }
                else {
                    orderService.cancelOrder(orderId)
                    logger.info("Canceled order: $orderId")
                    assignment.status = AssignmentStatus.COMPLETED
                }
                AssignmentActionResult(
                    assignment = assignment,
                    shouldContinue = true
                )
            }
        )
    }

    protected abstract fun getPrice(assignment: LA): MoneyPrice

    private fun getOrderId(assignment: LA): String {
        return assignment.info.orderId
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }
}
