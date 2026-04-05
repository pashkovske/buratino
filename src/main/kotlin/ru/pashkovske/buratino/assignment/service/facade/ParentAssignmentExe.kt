package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.ParentAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.service.core.build.ParentAssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter

abstract class ParentAssignmentExe<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment,
    ParentStartCmd : ParentAssignmentStartCmd<ParentA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    assignmentDao: AssignmentDao<ParentA>,
    assignmentRefresher: AssignmentRefresher<ParentA>,
    assignmentCanceller: AssignmentCanceller<ParentA>,
    assignmentStarter: AssignmentStarter<ParentA>,
    assignmentBuilder: ParentAssignmentBuilder<ParentA, ChildA, ParentStartCmd, ChildCmd>,
    protected val childAssignmentDao: AssignmentDao<ChildA>
) : BasicAssignmentExe<ParentA, ParentStartCmd>(
    assignmentDao = assignmentDao,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder
)