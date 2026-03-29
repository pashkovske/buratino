package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.build.ContinuousAssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignmentStartCmd

@Component
class ContinuousFractionalSpreadAssignmentBuilder(
    childBuilder: AssignmentBuilder<FractionalSpreadAssignment, FractionalSpreadAssignmentStartCmd>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ContinuousFractionalSpreadAssignment>,
    continueNotifyOrchestrator: ContinueNotifyOrchestrator<ContinuousFractionalSpreadAssignment, FractionalSpreadAssignment>
) : ContinuousAssignmentBuilder<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentStartCmd,
    FractionalSpreadAssignmentStartCmd
    >(
    childBuilder = childBuilder,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    continueNotifyOrchestrator = continueNotifyOrchestrator
) {

    override fun buildChildStartCmd(cmd: ContinuousFractionalSpreadAssignmentStartCmd): FractionalSpreadAssignmentStartCmd {
        return FractionalSpreadAssignmentStartCmd(
            iid = cmd.iid,
            direction = cmd.direction,
            rate = cmd.rate,
            refreshAssignmentSchedulingProperties = null
        )
    }

    override fun preBuildParent(
        cmd: ContinuousFractionalSpreadAssignmentStartCmd,
        child: FractionalSpreadAssignment
    ): ContinuousFractionalSpreadAssignment {
        return ContinuousFractionalSpreadAssignment.newAssignment(
            iid = cmd.iid,
            refreshAssignmentSchedulingProperties = cmd.refreshAssignmentSchedulingProperties,
            child = child,
            continueAssignmentSchedulingProperties = cmd.continueAssignmentSchedulingProperties
        )
    }
}
