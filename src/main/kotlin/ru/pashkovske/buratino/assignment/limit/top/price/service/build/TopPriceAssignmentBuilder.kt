package ru.pashkovske.buratino.assignment.limit.top.price.service.build

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.limit.base.service.build.LimitOrderAssignmentBuilder
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignmentStartCmd

@Component
class TopPriceAssignmentBuilder(
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<TopPriceAssignment>
) : LimitOrderAssignmentBuilder<
    TopPriceAssignment,
    TopPriceAssignmentStartCmd
    >(
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
