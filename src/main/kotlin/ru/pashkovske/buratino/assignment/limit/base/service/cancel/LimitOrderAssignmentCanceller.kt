package ru.pashkovske.buratino.assignment.limit.base.service.cancel

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.cancel.BasicAssignmentCanceller
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.order.service.OrderService

abstract class LimitOrderAssignmentCanceller<LimitA : LimitOrderAssignment>(
    assignmentDao: AssignmentDao<LimitA>,
    taskScheduler: TaskScheduler,
    private val orderService: OrderService
) : BasicAssignmentCanceller<LimitA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doCancel(ctx: ExeCtx<LimitA>) {
        cancelLimitOrder(ctx)
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

    private fun getOrderId(assignment: LimitA): String {
        return assignment.info.orderId
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }
}
