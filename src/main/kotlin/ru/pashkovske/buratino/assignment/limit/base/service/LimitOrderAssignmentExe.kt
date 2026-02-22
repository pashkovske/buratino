package ru.pashkovske.buratino.assignment.limit.base.service

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.common.utils.scheduler.TaskSchedulerFacade
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

abstract class LimitOrderAssignmentExe<LimitA : LimitOrderAssignment>(
    private val orderService: OrderService,
    assignmentDao: AssignmentDao<LimitA>,
    taskSchedulerFacade: TaskSchedulerFacade
): BasicAssignmentExe<LimitA>(
    assignmentDao = assignmentDao,
    taskSchedulerFacade = taskSchedulerFacade
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doStart(ctx: ExeCtx<LimitA>) {
        startLimitOrder(ctx)
    }

    override fun doRefresh(ctx: ExeCtx<LimitA>) {
        refreshLimitOrder(ctx)
    }

    override fun doCancel(ctx: ExeCtx<LimitA>) {
        cancelLimitOrder(ctx)
    }

    protected fun startLimitOrder(ctx: ExeCtx<LimitA>) {
        val assignment: LimitA = ctx.assignment
        val order: Order = orderService.createOrder(
            orderRequest = buildLimitReq(assignment)
        )
        assignment.info.orderId = order.id
        ctx.setMutated()
    }

    protected fun refreshLimitOrder(ctx: ExeCtx<LimitA>) {
        val assignment: LimitA = ctx.assignment
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            log.info("Order of assignment ${assignment.id} is already completed, skipping order refresh")
            postCancel(ctx)
            return
        }
        log.info("Refreshing order: $orderId in assignment ${assignment.id}")
        val newOrder: Order = orderService.replaceOrder(
            orderId = orderId,
            newOrderRequest = buildLimitReq(assignment)
        )
        log.info("Refreshed order: $orderId in assignment ${assignment.id}")
        assignment.info.orderId = newOrder.id
        ctx.setMutated()
    }

    protected fun cancelLimitOrder(ctx: ExeCtx<LimitA>) {
        val assignment: LimitA = ctx.assignment
        val orderId: String = getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            log.info("Order of assignment ${assignment.id} is already completed, skipping order cancel")
            return
        }
        orderService.cancelOrder(orderId)
        ctx.setMutated()
    }

    protected abstract fun getPrice(assignment: LimitA): Price

    private fun buildLimitReq(assignment: LimitA): LimitOrderRequest {
        return LimitOrderRequest(
            iid = assignment.iid,
            direction = assignment.direction,
            lots = 1,
            idempotencyToken = UUID.randomUUID(),
            price = getPrice(assignment)
        )
    }

    private fun getOrderId(assignment: LimitA): String {
        return assignment.info.orderId
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }
}
