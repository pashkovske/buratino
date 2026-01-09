package ru.pashkovske.buratino.assignment.limit.base.executor

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.model.AssignmentStatus
import ru.pashkovske.buratino.assignment.limit.base.model.LimitedOrderAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.base.exe.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.model.AssignmentCommandExeCtx
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

abstract class LimitOrderAssignmentExe<LA : LimitedOrderAssignment>(
    private val orderService: OrderService,
    assignmentRepo: AssignmentRepo<LA>,
    assignmentScheduler: AssignmentTaskScheduler
): BasicAssignmentExe<LA>(
    assignmentRepo = assignmentRepo,
    assignmentScheduler = assignmentScheduler
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doStart(ctx: AssignmentCommandExeCtx<LA>) {
        val assignment: LA = ctx.assignment
        startLimitOrder(assignment)
        scheduleRefresh(assignment)
        setStatusInProgress(assignment)

        ctx.setMutated()
    }

    override fun doRefresh(ctx: AssignmentCommandExeCtx<LA>) {
        val assignment: LA = ctx.assignment
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment ${assignment.id} is already completed. Skipping refresh")
            return
        }
        refreshLimitOrder(assignment)
        setStatusInProgress(assignment)

        ctx.setMutated()
    }

    override fun doCancel(ctx: AssignmentCommandExeCtx<LA>) {
        val assignment: LA = ctx.assignment
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment ${assignment.id} is already completed. Skipping cancel")
            return
        }
        stopSchedulingRefresh(assignment)
        cancelLimitOrder(assignment)
        setStatusCompleted(assignment)

        ctx.setMutated()
    }

    protected fun startLimitOrder(assignment: LA): LA {
        val order: Order = orderService.createOrder(
            orderRequest = buildLimitReq(assignment)
        )
        assignment.info.orderId = order.id
        return assignment
    }

    protected fun refreshLimitOrder(assignment: LA): LA {
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            log.info("Order of assignment ${assignment.id} is already completed, skipping order refresh")
            assignment.status = AssignmentStatus.COMPLETED
        }
        else {
            val newOrder: Order = orderService.replaceOrder(
                orderId = orderId,
                newOrderRequest = buildLimitReq(assignment)
            )
            log.info("Refreshed order: $orderId")
            assignment.status = AssignmentStatus.IN_PROGRESS
            assignment.info.orderId = newOrder.id
        }
        return assignment
    }

    protected fun cancelLimitOrder(assignment: LA): LA {
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            log.info("Order of assignment ${assignment.id} is already completed, skipping order cancel")
        }
        else {
            orderService.cancelOrder(orderId)
            log.info("Canceled order: $orderId")
            assignment.status = AssignmentStatus.COMPLETED
        }
        return assignment
    }

    protected abstract fun getPrice(assignment: LA): Price

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
