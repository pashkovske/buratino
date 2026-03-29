package ru.pashkovske.buratino.assignment.service.core.refresh

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

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