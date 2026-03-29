package ru.pashkovske.buratino.assignment.service.core.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.model.cmd.ContinuousFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

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