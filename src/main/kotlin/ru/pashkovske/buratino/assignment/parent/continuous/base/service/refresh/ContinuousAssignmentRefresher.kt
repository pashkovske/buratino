package ru.pashkovske.buratino.assignment.parent.continuous.base.service.refresh

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.parent.base.service.refresh.ParentAssignmentRefresher
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment

abstract class ContinuousAssignmentRefresher<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    childAssignmentDao: AssignmentDao<ChildA>,
    childAssignmentRefresher: AssignmentRefresher<ChildA>
) : ParentAssignmentRefresher<ChildA, ContinuousA>(
    assignmentDao = assignmentDao,
    childAssignmentDao = childAssignmentDao,
    childAssignmentRefresher = childAssignmentRefresher
)
