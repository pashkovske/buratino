package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.service.cancel

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.cancel.ContinuousAssignmentCanceller
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class ContinuousFractionalSpreadAssignmentCanceller(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    nestedAssignmentExe: FractionalSpreadAssignmentExe
) : ContinuousAssignmentCanceller<FractionalSpreadAssignment, ContinuousFractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    nestedAssignmentExe = nestedAssignmentExe,
    continuousTaskScheduler = taskScheduler
)
