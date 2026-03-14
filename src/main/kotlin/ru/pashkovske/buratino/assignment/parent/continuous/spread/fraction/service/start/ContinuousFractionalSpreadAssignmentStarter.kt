package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.start.ContinuousAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class ContinuousFractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<ContinuousFractionalSpreadAssignment>,
    childAssignmentExe: FractionalSpreadAssignmentExe,
    continuousAssignmentContinuer: ContinuousAssignmentContinuer<ContinuousFractionalSpreadAssignment>
) : ContinuousAssignmentStarter<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentStartCmd,
    FractionalSpreadAssignmentStartCmd
    >(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    childAssignmentExe = childAssignmentExe,
    continuousTaskScheduler = taskScheduler,
    continuousAssignmentContinuer = continuousAssignmentContinuer
) {
    override fun buildChildStartCmd(cmd: ContinuousFractionalSpreadAssignmentStartCmd): FractionalSpreadAssignmentStartCmd {
        return FractionalSpreadAssignmentStartCmd(
            iid = cmd.iid,
            direction = cmd.direction,
            rate = cmd.rate,
            refreshSchedulingProperties = null
        )
    }

    override fun buildParentAssignment(
        cmd: ContinuousFractionalSpreadAssignmentStartCmd,
        child: FractionalSpreadAssignment
    ): ContinuousFractionalSpreadAssignment {
        return ContinuousFractionalSpreadAssignment.newAssignment(
            iid = cmd.iid,
            refreshSchedulingProperties = cmd.refreshSchedulingProperties,
            child = child,
            continueSchedulingProperties = cmd.continueSchedulingProperties
        )
    }
}
