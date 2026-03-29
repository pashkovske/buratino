package ru.pashkovske.buratino.assignment.limit.top.price.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.limit.base.service.order.LimitOrderFactory
import ru.pashkovske.buratino.assignment.limit.base.service.start.LimitOrderAssignmentStarter
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.order.service.OrderService

@Service
class TopPriceAssignmentStarter(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<TopPriceAssignment>,
    orderService: OrderService,
    limitOrderFactory: LimitOrderFactory<TopPriceAssignment>
) : LimitOrderAssignmentStarter<TopPriceAssignment>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    orderService = orderService,
    limitOrderFactory = limitOrderFactory
)
