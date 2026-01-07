package ru.pashkovske.buratino.assignment.limit.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExe
import ru.pashkovske.buratino.assignment.limit.top.price.action.GetTopPriceActionExe
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceCtx
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignmentCtxMapper
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.model.Price

@Service
final class TopPriceAssignmentExe(
    orderService: OrderService,
    assignmentRepo: AssignmentRepo<TopPriceAssignment>,
    val getTopPriceActionExe: GetTopPriceActionExe,
    assignmentScheduler: AssignmentTaskScheduler
) : LimitOrderAssignmentExe<TopPriceAssignment>(
    orderService = orderService,
    assignmentRepo = assignmentRepo,
    assignmentScheduler = assignmentScheduler
) {
    override fun getPrice(assignment: TopPriceAssignment): Price {
        val ctx: TopPriceCtx = TopPriceAssignmentCtxMapper.map(assignment)
        getTopPriceActionExe.execute(ctx)
        return ctx.topPrice!!
    }
}
