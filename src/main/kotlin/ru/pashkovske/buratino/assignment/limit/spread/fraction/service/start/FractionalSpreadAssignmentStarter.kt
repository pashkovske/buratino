package ru.pashkovske.buratino.assignment.limit.spread.fraction.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.limit.base.service.start.LimitOrderAssignmentStarter
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.order.service.OrderService

@Service
class FractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<FractionalSpreadAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<FractionalSpreadAssignment>
) : LimitOrderAssignmentStarter<FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)
