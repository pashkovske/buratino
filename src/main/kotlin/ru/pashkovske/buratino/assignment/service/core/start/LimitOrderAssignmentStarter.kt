package ru.pashkovske.buratino.assignment.service.core.start

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.service.limit.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.service.OrderService

abstract class LimitOrderAssignmentStarter<LimitA : LimitOrderAssignment>(
    assignmentDao: AssignmentDao<LimitA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    private val orderService: OrderService,
    private val limitOrderFactory: LimitOrderFactory<LimitA>
) : BasicAssignmentStarter<LimitA>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    override fun doStart(ctx: ExeCtx<LimitA>) {
        startLimitOrder(ctx)
    }

    protected fun startLimitOrder(ctx: ExeCtx<LimitA>) {
        val assignment: LimitA = ctx.assignment
        val order: Order = orderService.createOrder(
            orderRequest = limitOrderFactory.buildLimitOrderRequest(assignment)
        )
        assignment.info.orderId = order.id
        ctx.setMutated()
    }
}