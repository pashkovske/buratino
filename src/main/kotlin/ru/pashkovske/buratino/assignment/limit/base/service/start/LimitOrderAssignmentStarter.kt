package ru.pashkovske.buratino.assignment.limit.base.service.start

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.BasicAssignmentStarter
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

abstract class LimitOrderAssignmentStarter<LimitA : LimitOrderAssignment>(
    assignmentDao: AssignmentDao<LimitA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<LimitA>,
    private val orderService: OrderService
) : BasicAssignmentStarter<LimitA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher
) {

    override fun doStart(ctx: ExeCtx<LimitA>) {
        startLimitOrder(ctx)
    }

    protected fun startLimitOrder(ctx: ExeCtx<LimitA>) {
        val assignment: LimitA = ctx.assignment
        val order: Order = orderService.createOrder(
            orderRequest = buildLimitReq(assignment)
        )
        assignment.info.orderId = order.id
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
}
