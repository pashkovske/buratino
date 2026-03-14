package ru.pashkovske.buratino.assignment.limit.top.price.service.refresh

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.limit.base.service.refresh.LimitOrderAssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.order.service.OrderService

@Service
class TopPriceAssignmentRefresher(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<TopPriceAssignment>
) : LimitOrderAssignmentRefresher<TopPriceAssignment>(
    assignmentDao = assignmentDao,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)
