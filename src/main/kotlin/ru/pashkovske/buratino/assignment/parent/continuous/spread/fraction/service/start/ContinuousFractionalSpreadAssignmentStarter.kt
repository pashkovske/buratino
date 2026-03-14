package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.start.ContinuousAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class ContinuousFractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ContinuousFractionalSpreadAssignment>,
    childAssignmentExe: FractionalSpreadAssignmentExe,
    continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousFractionalSpreadAssignment>
) : ContinuousAssignmentStarter<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    childAssignmentExe = childAssignmentExe,
    continuousTaskScheduler = taskScheduler,
    continuousAssignmentContinuer = continuousAssignmentContinuer
)
