package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.`continue`

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.ExeCtx
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.build.FractionalSpreadAssignmentBuilder
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.start.FractionalSpreadAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.BasicContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment

@Service
class ContinuousFractionalSpreadAssignmentContinuer(
    assignmentDao: AssignmentDao<ContinuousFractionalSpreadAssignment>,
    childAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    private val childAssignmentBuilder: FractionalSpreadAssignmentBuilder,
    private val childAssignmentStarter: FractionalSpreadAssignmentStarter
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
        val nextAssignmentCmd = FractionalSpreadAssignmentStartCmd(
            iid = completedAssignment.iid,
            direction = completedAssignment.direction.getOpposite(),
            rate = completedAssignment.rate,
            refreshSchedulingProperties = null
        )
        val nextAssignment: FractionalSpreadAssignment = childAssignmentBuilder.build(nextAssignmentCmd)
        ctx.assignment.child = childAssignmentStarter.start(nextAssignment)
        log.info("Started new spread fraction assignment: ${nextAssignment.id}")
        ctx.setMutated()
    }
}
