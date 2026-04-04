package ru.pashkovske.buratino.assignment.service.core.start

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

abstract class ParentAssignmentStarter<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ParentA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : BasicAssignmentStarter<ParentA>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)