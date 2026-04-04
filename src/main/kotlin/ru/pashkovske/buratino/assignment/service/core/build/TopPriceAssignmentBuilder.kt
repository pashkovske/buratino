package ru.pashkovske.buratino.assignment.service.core.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.TopPriceAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

@Component
class TopPriceAssignmentBuilder(
    assignmentDao: AssignmentDao<TopPriceAssignment>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : LimitOrderAssignmentBuilder<
    TopPriceAssignment,
    TopPriceAssignmentStartCmd
    >(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
) {

    override fun preBuild(cmd: TopPriceAssignmentStartCmd): TopPriceAssignment {
        return TopPriceAssignment.newAssignment(
            iid = cmd.iid,
            direction = cmd.direction,
            refreshAssignmentSchedulingProperties = cmd.refreshAssignmentSchedulingProperties,
            oneStepOver = cmd.oneStepOver
        )
    }
}