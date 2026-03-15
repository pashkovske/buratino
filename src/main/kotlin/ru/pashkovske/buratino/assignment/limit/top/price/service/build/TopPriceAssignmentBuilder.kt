package ru.pashkovske.buratino.assignment.limit.top.price.service.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.limit.base.service.build.LimitOrderAssignmentBuilder
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignmentStartCmd

@Component
class TopPriceAssignmentBuilder : LimitOrderAssignmentBuilder<
    TopPriceAssignment,
    TopPriceAssignmentStartCmd
    >() {

    override fun build(cmd: TopPriceAssignmentStartCmd): TopPriceAssignment {
        return TopPriceAssignment.newAssignment(
            iid = cmd.iid,
            direction = cmd.direction,
            refreshSchedulingProperties = cmd.refreshSchedulingProperties,
            oneStepOver = cmd.oneStepOver
        )
    }
}
