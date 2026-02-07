package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.BasicContinuousAssignmentExe
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe

@Service
final class ContinuousFractionalSpreadAssignmentExe(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    nestedAssignmentExe: FractionalSpreadAssignmentExe,
    assignmentScheduler: AssignmentTaskScheduler,
    nestedAssignmentDao: AssignmentDao<FractionalSpreadAssignment>
): BasicContinuousAssignmentExe<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment
    >(
    assignmentDao = assignmentDao,
    nestedAssignmentExe = nestedAssignmentExe,
    assignmentScheduler = assignmentScheduler,
    nestedAssignmentDao = nestedAssignmentDao
) {

    private val log = KotlinLogging.logger {}

    private fun startNewSpreadFractionAssignment(ctx: ExeCtx<ContinuousFractionalSpreadAssignment>) {
        val completedAssignment: FractionalSpreadAssignment = ctx.assignment.nested
        val nextAssignment = FractionalSpreadAssignment.newAssignment(
            iid = completedAssignment.iid,
            refreshSchedulingProperties = null,
            direction = completedAssignment.direction.getOpposite(),
            rate = completedAssignment.rate
        )
        nestedAssignmentExe.start(nextAssignment)
        ctx.assignment.nested = nextAssignment
        log.info("Started new spread fraction assignment: ${nextAssignment.id}")
        ctx.setMutated()
    }

    override fun doStart(
        ctx: ExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        checkAndStartNested(ctx)
    }

    override fun doRefresh(
        ctx: ExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        if (isNestedCompleted(ctx)) {
            log.info("Nested assignment ${ctx.assignment.nested.id} is completed. Skipping refresh")
            return
        }
        refreshNested(ctx)
    }

    override fun doCancel(
        ctx: ExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        cancelNested(ctx)
    }

    override fun doContinue(
        ctx: ExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        if (!isNestedCompleted(ctx)) {
            log.warn("Nested assignment ${ctx.assignment.nested.id} is not completed. Skipping continue")
            return
        }
        startNewSpreadFractionAssignment(ctx)
    }
}
