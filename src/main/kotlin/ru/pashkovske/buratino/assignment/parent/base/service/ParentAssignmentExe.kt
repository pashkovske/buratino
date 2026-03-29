package ru.pashkovske.buratino.assignment.parent.base.service

import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignmentStartCmd
import ru.pashkovske.buratino.assignment.parent.base.service.builder.ParentAssignmentBuilder

abstract class ParentAssignmentExe<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment,
    ParentStartCmd : ParentAssignmentStartCmd<ParentA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    assignmentDao: AssignmentDao<ParentA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ParentA>,
    assignmentRefresher: AssignmentRefresher<ParentA>,
    assignmentCanceller: AssignmentCanceller<ParentA>,
    assignmentStarter: AssignmentStarter<ParentA>,
    assignmentBuilder: ParentAssignmentBuilder<ParentA, ChildA, ParentStartCmd, ChildCmd>,
    protected val childAssignmentDao: AssignmentDao<ChildA>
) : BasicAssignmentExe<ParentA, ParentStartCmd>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder
)
