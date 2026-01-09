package ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.exe

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.base.service.AssignmentTaskScheduler
import ru.pashkovske.buratino.assignment.nested.continuous.base.exe.BasicContinuousAssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.exe.FractionalSpreadAssignmentExe
import java.util.UUID

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

    override fun start(assignment: ContinuousFractionalSpreadAssignment): ContinuousFractionalSpreadAssignment {
        logStart(assignment)
        checkAndStartNested(assignment)
        scheduleContinue(assignment)
        scheduleRefresh(assignment)
        setStatusInProgress(assignment)
        createInRepo(assignment)
        return assignment
    }

    override fun refresh(id: UUID): ContinuousFractionalSpreadAssignment {
        val assignment: ContinuousFractionalSpreadAssignment = getFromRepo(id)
        logRefresh(assignment)
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment $id is already completed. Skipping refresh")
            return assignment
        }
        val isNestedCompleted: Boolean = checkNestedCompleted(assignment)
        if (isNestedCompleted) {
            log.info("Nested assignment ${assignment.nested.id} is completed. Skipping refresh")
            return assignment
        }
        refreshNested(assignment)
        setStatusInProgress(assignment)
        updateInRepo(assignment)
        return assignment
    }

    override fun cancel(id: UUID): ContinuousFractionalSpreadAssignment {
        val assignment: ContinuousFractionalSpreadAssignment = getFromRepo(id)
        logCancel(assignment)
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment $id is already completed. Skipping cancel")
            return assignment
        }
        stopSchedulingContinuation(assignment)
        stopSchedulingRefresh(assignment)
        cancelNested(assignment)
        setStatusCompleted(assignment)
        updateInRepo(assignment)
        return assignment
    }

    override fun continueAssignment(id: UUID): ContinuousFractionalSpreadAssignment {
        val assignment: ContinuousFractionalSpreadAssignment = getFromRepo(id)
        logContinue(assignment)
        val isCompleted: Boolean = checkCompleted(assignment)
        if (isCompleted) {
            log.warn("Assignment $id is already completed. Skipping continue")
            return assignment
        }
        val isNestedCompleted: Boolean = checkNestedCompleted(assignment)
        if (!isNestedCompleted) {
            log.warn("Nested assignment ${assignment.nested.id} is not completed. Skipping continue")
            return assignment
        }
        startNewSpreadFractionAssignment(assignment)
        updateInRepo(assignment)
        return assignment
    }
}
