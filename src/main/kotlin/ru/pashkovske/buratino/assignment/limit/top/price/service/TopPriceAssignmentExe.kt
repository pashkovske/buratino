package ru.pashkovske.buratino.assignment.limit.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExe

@Service
final class TopPriceAssignmentExe(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<TopPriceAssignment>,
    assignmentCanceller: AssignmentCanceller<TopPriceAssignment>,
    assignmentStarter: AssignmentStarter<TopPriceAssignment>
) : LimitOrderAssignmentExe<TopPriceAssignment>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter
)
