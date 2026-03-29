package ru.pashkovske.buratino.assignment.limit.base.service.start

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.start.BasicAssignmentStarter
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.service.OrderService

abstract class LimitOrderAssignmentStarter<LimitA : LimitOrderAssignment>(
    assignmentDao: AssignmentDao<LimitA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<LimitA>,
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
