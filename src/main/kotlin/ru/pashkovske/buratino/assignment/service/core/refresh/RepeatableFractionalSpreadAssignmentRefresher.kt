package ru.pashkovske.buratino.assignment.service.core.refresh

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.facade.FractionalSpreadAssignmentExe

@Service
class RepeatableFractionalSpreadAssignmentRefresher(
    assignmentDao: AssignmentDao<RepeatableFractionalSpreadAssignment>,
    childAssignmentDao: AssignmentDao<FractionalSpreadAssignment>,
    childExe: FractionalSpreadAssignmentExe
) : RepeatableAssignmentRefresher<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentStartCmd,
    RepeatableFractionalSpreadAssignment
    >(
    assignmentDao = assignmentDao,
    childAssignmentDao = childAssignmentDao,
    childExe = childExe
) {

    private val log: KLogger = KotlinLogging.logger {}

    override fun doRefresh(
        ctx: ExeCtx<RepeatableFractionalSpreadAssignment>
    ) {
        if (isChildCompleted(ctx)) {
            startNewSpreadFractionAssignment(ctx)
        } else {
            refreshChild(ctx)
        }
    }

    private fun startNewSpreadFractionAssignment(ctx: ExeCtx<RepeatableFractionalSpreadAssignment>) {
        val completedAssignment: FractionalSpreadAssignment = ctx.assignment.child
        val nextAssignmentCmd = FractionalSpreadAssignmentStartCmd(
            iid = completedAssignment.iid,
            direction = completedAssignment.direction.getOpposite(),
            rate = completedAssignment.rate,
            refreshNotifierProperties = null
        )
        val nextAssignment: FractionalSpreadAssignment = childExe.build(nextAssignmentCmd)
        ctx.assignment.child = childExe.start(nextAssignment.id)
        log.info("Started new spread fraction assignment: ${nextAssignment.id}")
        ctx.setMutated()
    }
}
