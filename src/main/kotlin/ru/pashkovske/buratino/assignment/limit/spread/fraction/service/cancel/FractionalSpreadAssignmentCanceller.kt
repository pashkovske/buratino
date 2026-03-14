package ru.pashkovske.buratino.assignment.limit.spread.fraction.service.cancel

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.limit.base.service.cancel.LimitOrderAssignmentCanceller
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.order.service.OrderService

@Service
class FractionalSpreadAssignmentCanceller(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<FractionalSpreadAssignment>
) : LimitOrderAssignmentCanceller<FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)
