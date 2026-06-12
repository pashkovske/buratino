package ru.pashkovske.buratino.assignment.service.core.cancel

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.service.limit.order.LimitOrderFactory
import ru.pashkovske.buratino.order.service.OrderService

abstract class LimitOrderAssignmentCanceller<LimitA : LimitOrderAssignment>(
    assignmentDao: AssignmentDao<LimitA>,
    private val orderService: OrderService,
    private val limitOrderFactory: LimitOrderFactory<LimitA>
) : BasicAssignmentCanceller<LimitA>(
    assignmentDao = assignmentDao
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doCancel(ctx: ExeCtx<LimitA>) {
        cancelLimitOrder(ctx)
    }

    protected fun cancelLimitOrder(ctx: ExeCtx<LimitA>) {
        val assignment: LimitA = ctx.assignment
        val orderId: String = limitOrderFactory.getOrderId(assignment)
        if (orderService.isOrderCompleted(orderId)) {
            log.info("Order of assignment ${assignment.id} is already completed, skipping order cancel")
            return
        }
        orderService.cancelOrder(orderId)
        ctx.setMutated()
    }
}