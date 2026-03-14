package ru.pashkovske.buratino.assignment.limit.base.service.refresh

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.refresh.BasicAssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.service.OrderService

abstract class LimitOrderAssignmentRefresher<LimitA : LimitOrderAssignment>(
    assignmentDao: AssignmentDao<LimitA>,
    private val orderService: OrderService,
    private val limitOrderFactory: LimitOrderFactory<LimitA>
) : BasicAssignmentRefresher<LimitA>(
    assignmentDao = assignmentDao
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doRefresh(ctx: ExeCtx<LimitA>) {
        refreshLimitOrder(ctx)
    }

    protected fun refreshLimitOrder(ctx: ExeCtx<LimitA>) {
        val assignment: LimitA = ctx.assignment
        val orderId: String = limitOrderFactory.getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            log.info("Order of assignment ${assignment.id} is already completed, skipping order refresh")
            postRefresh(ctx)
            return
        }
        log.info("Refreshing order: $orderId in assignment ${assignment.id}")
        val newOrder: Order = orderService.replaceOrder(
            orderId = orderId,
            newOrderRequest = limitOrderFactory.buildLimitOrderRequest(assignment)
        )
        log.info("Refreshed order: $orderId in assignment ${assignment.id}")
        assignment.info.orderId = newOrder.id
        ctx.setMutated()
    }
}
