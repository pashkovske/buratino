package ru.pashkovske.buratino.assignment.service.core.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.RepeatableFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

@Component
class RepeatableFractionalSpreadAssignmentBuilder(
    childExe: AssignmentExe<FractionalSpreadAssignment, FractionalSpreadAssignmentStartCmd>,
    assignmentDao: AssignmentDao<RepeatableFractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
) : RepeatableAssignmentBuilder<
    RepeatableFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignmentStartCmd,
    FractionalSpreadAssignmentStartCmd
    >(
    childExe = childExe,
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    override fun buildChildStartCmd(cmd: RepeatableFractionalSpreadAssignmentStartCmd): FractionalSpreadAssignmentStartCmd {
        return FractionalSpreadAssignmentStartCmd(
            iid = cmd.iid,
            direction = cmd.direction,
            rate = cmd.rate,
            refreshNotifierProperties = null
        )
    }

    override fun preBuildParent(
        cmd: RepeatableFractionalSpreadAssignmentStartCmd,
        child: FractionalSpreadAssignment
    ): RepeatableFractionalSpreadAssignment {
        return RepeatableFractionalSpreadAssignment.newAssignment(
            iid = cmd.iid,
            child = child
        )
    }
}
