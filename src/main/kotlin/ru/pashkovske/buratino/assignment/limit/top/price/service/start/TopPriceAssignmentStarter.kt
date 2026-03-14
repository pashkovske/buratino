package ru.pashkovske.buratino.assignment.limit.top.price.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.limit.base.service.start.LimitOrderAssignmentStarter
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignmentStartCmd
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.order.service.OrderService

@Service
class TopPriceAssignmentStarter(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<TopPriceAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<TopPriceAssignment>
) : LimitOrderAssignmentStarter<TopPriceAssignment, TopPriceAssignmentStartCmd>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
) {
    override fun buildAssignment(cmd: TopPriceAssignmentStartCmd): TopPriceAssignment {
        return TopPriceAssignment.newAssignment(
            iid = cmd.iid,
            direction = cmd.direction,
            refreshSchedulingProperties = cmd.refreshSchedulingProperties,
            oneStepOver = cmd.oneStepOver
        )
    }
}
