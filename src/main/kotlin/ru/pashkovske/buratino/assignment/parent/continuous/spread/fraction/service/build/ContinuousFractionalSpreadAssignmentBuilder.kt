package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.build.ContinuousAssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignmentStartCmd

@Component
class ContinuousFractionalSpreadAssignmentBuilder(
    childBuilder: AssignmentBuilder<FractionalSpreadAssignment, FractionalSpreadAssignmentStartCmd>
) : ContinuousAssignmentBuilder<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignmentStartCmd,
    FractionalSpreadAssignmentStartCmd
    >(
    childBuilder = childBuilder
) {

    override fun buildChildStartCmd(cmd: ContinuousFractionalSpreadAssignmentStartCmd): FractionalSpreadAssignmentStartCmd {
        return FractionalSpreadAssignmentStartCmd(
            iid = cmd.iid,
            direction = cmd.direction,
            rate = cmd.rate,
            refreshSchedulingProperties = null
        )
    }

    override fun buildParent(
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
