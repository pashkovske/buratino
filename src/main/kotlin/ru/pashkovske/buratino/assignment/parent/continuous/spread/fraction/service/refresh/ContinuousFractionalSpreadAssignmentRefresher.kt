package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.refresh

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.refresh.FractionalSpreadAssignmentRefresher
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.refresh.ContinuousAssignmentRefresher
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentRefresher(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    childAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    childAssignmentRefresher: FractionalSpreadAssignmentRefresher
) : ContinuousAssignmentRefresher<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment
    >(
    assignmentDao = assignmentDao,
    childAssignmentDao = childAssignmentDao,
    childAssignmentRefresher = childAssignmentRefresher
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doRefresh(
        ctx: ExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        if (isChildCompleted(ctx)) {
            log.info("Child assignment ${ctx.assignment.child.id} is completed. Skipping refresh")
            return
        }
        refreshChild(ctx)
    }
}
