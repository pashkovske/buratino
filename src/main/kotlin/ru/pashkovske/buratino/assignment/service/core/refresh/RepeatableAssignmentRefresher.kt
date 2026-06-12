package ru.pashkovske.buratino.assignment.service.core.refresh

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment
import ru.pashkovske.buratino.assignment.service.facade.AssignmentExe

abstract class RepeatableAssignmentRefresher<
    ChildA : Assignment,
    ChildCmd: AssignmentStartCmd<ChildA>,
    RepeatableA : RepeatableAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<RepeatableA>,
    childAssignmentDao: AssignmentDao<ChildA>,
    childExe: AssignmentExe<ChildA, ChildCmd>
) : ParentAssignmentRefresher<ChildA, ChildCmd, RepeatableA>(
    assignmentDao = assignmentDao,
    childAssignmentDao = childAssignmentDao,
    childExe = childExe
)
