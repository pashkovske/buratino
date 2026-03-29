package ru.pashkovske.buratino.assignment.service.core.refresh

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment

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