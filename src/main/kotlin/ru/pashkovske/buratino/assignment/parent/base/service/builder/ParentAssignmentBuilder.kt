package ru.pashkovske.buratino.assignment.parent.base.service.builder

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignmentStartCmd

abstract class ParentAssignmentBuilder<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment,
    ParentCmd : ParentAssignmentStartCmd<ParentA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    protected val childBuilder: AssignmentBuilder<ChildA, ChildCmd>
) : AssignmentBuilder<ParentA, ParentCmd> {

    protected abstract fun buildChildStartCmd(cmd: ParentCmd): ChildCmd

    protected abstract fun buildParent(cmd: ParentCmd, child: ChildA): ParentA

    final override fun build(cmd: ParentCmd): ParentA {
        val childStartCmd = buildChildStartCmd(cmd)
        val childAssignment = childBuilder.build(childStartCmd)
        return buildParent(cmd, childAssignment)
    }
}
