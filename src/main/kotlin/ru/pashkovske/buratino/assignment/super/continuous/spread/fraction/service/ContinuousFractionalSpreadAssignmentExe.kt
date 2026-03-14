package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.BasicContinuousAssignmentExe
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe

@Service
final class ContinuousFractionalSpreadAssignmentExe(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ContinuousFractionalSpreadAssignment>,
    assignmentCanceller: AssignmentCanceller<ContinuousFractionalSpreadAssignment>,
    assignmentStarter: AssignmentStarter<ContinuousFractionalSpreadAssignment>,
    nestedAssignmentExe: FractionalSpreadAssignmentExe,
    nestedAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousFractionalSpreadAssignment>
): BasicContinuousAssignmentExe<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment
    >(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    nestedAssignmentExe = nestedAssignmentExe,
    nestedAssignmentDao = nestedAssignmentDao,
    continuousAssignmentContinuer = continuousAssignmentContinuer
)
