package ru.pashkovske.buratino.assignment.limit.spread.fraction.service.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.limit.base.service.build.LimitOrderAssignmentBuilder
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd

@Component
class FractionalSpreadAssignmentBuilder : LimitOrderAssignmentBuilder<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentStartCmd
    >() {

    override fun build(cmd: FractionalSpreadAssignmentStartCmd): FractionalSpreadAssignment {
        return FractionalSpreadAssignment.newAssignment(
            iid = cmd.iid,
            direction = cmd.direction,
            refreshSchedulingProperties = cmd.refreshSchedulingProperties,
            rate = cmd.rate
        )
    }
}
