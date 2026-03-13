package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.service.refresh

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.refresh.FractionalSpreadAssignmentRefresher
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.refresh.ContinuousAssignmentRefresher
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentRefresher(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    nestedAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    nestedAssignmentRefresher: FractionalSpreadAssignmentRefresher
) : ContinuousAssignmentRefresher<
    FractionalSpreadAssignment,
    ContinuousFractionalSpreadAssignment
    >(
    assignmentDao = assignmentDao,
    nestedAssignmentDao = nestedAssignmentDao,
    nestedAssignmentRefresher = nestedAssignmentRefresher
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doRefresh(
        ctx: ExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        if (isNestedCompleted(ctx)) {
            log.info("Nested assignment ${ctx.assignment.nested.id} is completed. Skipping refresh")
            return
        }
        refreshNested(ctx)
    }
}
