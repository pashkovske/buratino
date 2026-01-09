package ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.exe

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.AssignmentCommandExeCtx
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.scheduling.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.nested.continuous.base.exe.BasicContinuousAssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.exe.FractionalSpreadAssignmentExe

@Service
final class ContinuousFractionalSpreadAssignmentExe(
    assignmentRepo: AssignmentRepo<ContinuousFractionalSpreadAssignment>,
    nestedAssignmentExe: FractionalSpreadAssignmentExe,
    assignmentScheduler: AssignmentTaskScheduler
): BasicContinuousAssignmentExe<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment
    >(
    assignmentRepo = assignmentRepo,
    nestedAssignmentExe = nestedAssignmentExe,
    assignmentScheduler = assignmentScheduler
) {

    private val log = KotlinLogging.logger {}

    private fun startNewSpreadFractionAssignment(assignment: ContinuousFractionalSpreadAssignment): ContinuousFractionalSpreadAssignment {
        val completedAssignment = assignment.nested
        val nextAssignment = FractionalSpreadAssignment(
            iid = completedAssignment.iid,
            refreshSchedulingProperties = null,
            direction = completedAssignment.direction.getOpposite(),
            rate = completedAssignment.rate
        )
        nestedAssignmentExe.start(nextAssignment)
        assignment.nested = nextAssignment
        log.info("Started new spread fraction assignment: ${nextAssignment.id}")
        return assignment
    }

    override fun doStart(
        ctx: AssignmentCommandExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        val assignment: ContinuousFractionalSpreadAssignment = ctx.assignment
        checkAndStartNested(assignment)
        scheduleContinue(assignment)
        scheduleRefresh(assignment)
        setStatusInProgress(assignment)

        ctx.setMutated()
    }

    override fun doRefresh(
        ctx: AssignmentCommandExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        val assignment: ContinuousFractionalSpreadAssignment = ctx.assignment
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment ${assignment.id} is already completed. Skipping refresh")
            return
        }
        val isNestedCompleted: Boolean = checkNestedCompleted(assignment)
        if (isNestedCompleted) {
            log.info("Nested assignment ${assignment.nested.id} is completed. Skipping refresh")
        }
        refreshNested(assignment)
        setStatusInProgress(assignment)

        ctx.setMutated()
    }

    override fun doCancel(
        ctx: AssignmentCommandExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        val assignment: ContinuousFractionalSpreadAssignment = ctx.assignment
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment ${assignment.id} is already completed. Skipping cancel")
            return
        }
        stopSchedulingContinuation(assignment)
        stopSchedulingRefresh(assignment)
        cancelNested(assignment)
        setStatusCompleted(assignment)

        ctx.setMutated()
    }

    override fun doContinue(
        ctx: AssignmentCommandExeCtx<ContinuousFractionalSpreadAssignment>
    ) {
        val assignment: ContinuousFractionalSpreadAssignment = ctx.assignment
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment ${assignment.id} is already completed. Skipping continue")
            return
        }
        val isNestedCompleted: Boolean = checkNestedCompleted(assignment)
        if (!isNestedCompleted) {
            log.warn("Nested assignment ${assignment.nested.id} is not completed. Skipping continue")
            return
        }
        startNewSpreadFractionAssignment(assignment)

        ctx.setMutated()
    }
}
