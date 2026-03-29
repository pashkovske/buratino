package ru.pashkovske.buratino.assignment.parent.base.service.start

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.start.BasicAssignmentStarter
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignment

abstract class ParentAssignmentStarter<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<ParentA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<ParentA>
) : BasicAssignmentStarter<ParentA>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)
