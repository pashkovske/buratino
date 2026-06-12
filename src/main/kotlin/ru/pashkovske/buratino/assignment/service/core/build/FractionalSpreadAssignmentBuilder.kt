package ru.pashkovske.buratino.assignment.service.core.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

@Component
class FractionalSpreadAssignmentBuilder(
    assignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : LimitOrderAssignmentBuilder<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentStartCmd
    >(
        assignmentDao = assignmentDao,
        refreshNotifyOrchestrator = refreshNotifyOrchestrator
    ) {

    override fun preBuild(cmd: FractionalSpreadAssignmentStartCmd): FractionalSpreadAssignment {
        return FractionalSpreadAssignment.newAssignment(
            iid = cmd.iid,
            direction = cmd.direction,
            rate = cmd.rate
        )
    }
}