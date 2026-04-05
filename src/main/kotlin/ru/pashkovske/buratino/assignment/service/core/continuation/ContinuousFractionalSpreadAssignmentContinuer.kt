package ru.pashkovske.buratino.assignment.service.core.continuation

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.service.core.build.FractionalSpreadAssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.start.FractionalSpreadAssignmentStarter
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

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
            refreshNotifierProperties = null
        )
        val nextAssignment: FractionalSpreadAssignment = childAssignmentBuilder.build(nextAssignmentCmd)
        ctx.assignment.child = childAssignmentStarter.start(nextAssignment)
        log.info("Started new spread fraction assignment: ${nextAssignment.id}")
        ctx.setMutated()
    }
}