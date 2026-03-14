package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.`continue`

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.BasicContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentContinuer(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    childAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    private val childAssignmentExe: FractionalSpreadAssignmentExe
) : BasicContinuousAssignmentContinuer<FractionalSpreadAssignment, ContinuousFractionalSpreadAssignment>(
    assignmentDao = assignmentDao,
    childAssignmentDao = childAssignmentDao
) {

    private val log = KotlinLogging.logger {}

    override fun doContinue(ctx: ExeCtx<ContinuousFractionalSpreadAssignment>) {
        if (!isChildCompleted(ctx)) {
            log.warn("Child assignment ${ctx.assignment.child.id} is not completed. Skipping continue")
            return
        }
        startNewSpreadFractionAssignment(ctx)
    }

    private fun startNewSpreadFractionAssignment(ctx: ExeCtx<ContinuousFractionalSpreadAssignment>) {
        val completedAssignment: FractionalSpreadAssignment = ctx.assignment.child
        val nextAssignment = FractionalSpreadAssignment.newAssignment(
            iid = completedAssignment.iid,
            refreshSchedulingProperties = null,
            direction = completedAssignment.direction.getOpposite(),
            rate = completedAssignment.rate
        )
        childAssignmentExe.start(nextAssignment)
        ctx.assignment.child = nextAssignment
        log.info("Started new spread fraction assignment: ${nextAssignment.id}")
        ctx.setMutated()
    }
}
