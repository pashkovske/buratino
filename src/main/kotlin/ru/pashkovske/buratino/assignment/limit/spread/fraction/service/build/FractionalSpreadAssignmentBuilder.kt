package ru.pashkovske.buratino.assignment.limit.spread.fraction.service.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.limit.base.service.build.LimitOrderAssignmentBuilder
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd

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
