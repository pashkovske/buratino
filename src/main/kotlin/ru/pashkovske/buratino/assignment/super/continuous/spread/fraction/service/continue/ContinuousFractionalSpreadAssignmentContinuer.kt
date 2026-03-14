package ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.service.`continue`

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.`super`.continuous.base.service.`continue`.BasicContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.`super`.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentContinuer(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    nestedAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    private val nestedAssignmentExe: FractionalSpreadAssignmentExe
) : BasicContinuousAssignmentContinuer<FractionalSpreadAssignment, ContinuousFractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    nestedAssignmentDao = nestedAssignmentDao
) {

    private val log = KotlinLogging.logger {}

    override fun doContinue(ctx: ExeCtx<ContinuousFractionalSpreadAssignment>) {
        if (!isNestedCompleted(ctx)) {
            log.warn("Nested assignment ${ctx.assignment.nested.id} is not completed. Skipping continue")
            return
        }
        startNewSpreadFractionAssignment(ctx)
    }

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
}
