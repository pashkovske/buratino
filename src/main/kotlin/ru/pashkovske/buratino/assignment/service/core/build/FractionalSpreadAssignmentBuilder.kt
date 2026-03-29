package ru.pashkovske.buratino.assignment.service.core.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

@Component
class FractionalSpreadAssignmentBuilder(
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<FractionalSpreadAssignment>
) : LimitOrderAssignmentBuilder<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentStartCmd
    >(
        refreshNotifyOrchestrator = refreshNotifyOrchestrator
    ) {

    override fun preBuild(cmd: FractionalSpreadAssignmentStartCmd): FractionalSpreadAssignment {
        return FractionalSpreadAssignment.newAssignment(
            iid = cmd.iid,
            direction = cmd.direction,
            refreshAssignmentSchedulingProperties = cmd.refreshAssignmentSchedulingProperties,
            rate = cmd.rate
        )
    }
}