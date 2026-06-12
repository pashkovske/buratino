package ru.pashkovske.buratino.assignment.service.core.start

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.ExeCtx
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe

@Service
class RepeatableFractionalSpreadAssignmentStarter(
    assignmentDao: AssignmentDao<RepeatableFractionalSpreadAssignment>,
    private val childAssignmentExe: AssignmentExe<FractionalSpreadAssignment, FractionalSpreadAssignmentStartCmd>
) : RepeatableAssignmentStarter<
    RepeatableFractionalSpreadAssignment,
    FractionalSpreadAssignment
    >(
    assignmentDao = assignmentDao
) {

    override fun doStart(ctx: ExeCtx<RepeatableFractionalSpreadAssignment>) {
        ctx.assignment.child = childAssignmentExe.start(ctx.assignment.child.id)
    }
}
