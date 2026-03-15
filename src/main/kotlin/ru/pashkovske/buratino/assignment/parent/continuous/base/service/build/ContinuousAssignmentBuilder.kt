package ru.pashkovske.buratino.assignment.parent.continuous.base.service.build

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.base.service.builder.ParentAssignmentBuilder
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignmentStartCmd

abstract class ContinuousAssignmentBuilder<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment,
    ContinuousCmd : ContinuousAssignmentStartCmd<ContinuousA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    childBuilder: AssignmentBuilder<ChildA, ChildCmd>
) : ParentAssignmentBuilder<ContinuousA, ChildA, ContinuousCmd, ChildCmd>(
    childBuilder = childBuilder
)
