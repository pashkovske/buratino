package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.RepeatableAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

abstract class RepeatableAssignmentBuilder<
    RepeatableA : RepeatableAssignment<ChildA>,
    ChildA : Assignment,
    RepeatableCmd : RepeatableAssignmentStartCmd<RepeatableA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    childExe: AssignmentExe<ChildA, ChildCmd>,
    assignmentDao: AssignmentDao<RepeatableA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : ParentAssignmentBuilder<RepeatableA, ChildA, RepeatableCmd, ChildCmd>(
    childExe = childExe,
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)
