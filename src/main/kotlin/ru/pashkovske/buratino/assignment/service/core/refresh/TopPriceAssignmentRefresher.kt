package ru.pashkovske.buratino.assignment.service.core.refresh

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.limit.order.LimitOrderFactory
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
