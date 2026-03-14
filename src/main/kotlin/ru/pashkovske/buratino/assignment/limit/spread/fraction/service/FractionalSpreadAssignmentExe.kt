package ru.pashkovske.buratino.assignment.limit.spread.fraction.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.limit.base.service.LimitOrderAssignmentExe
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment

@Service
final class FractionalSpreadAssignmentExe(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<FractionalSpreadAssignment>,
    assignmentCanceller: AssignmentCanceller<FractionalSpreadAssignment>,
    assignmentStarter: AssignmentStarter<FractionalSpreadAssignment>
): LimitOrderAssignmentExe<FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter
)
