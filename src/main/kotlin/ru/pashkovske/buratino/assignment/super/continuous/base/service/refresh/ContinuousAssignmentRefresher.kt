package ru.pashkovske.buratino.assignment.`super`.continuous.base.service.refresh

import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.`super`.base.service.refresh.SuperAssignmentRefresher
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment

abstract class ContinuousAssignmentRefresher<
    NestedA : Assignment,
    ContinuousA : ContinuousAssignment<NestedA>
    >(
    assignmentDao: AssignmentDao<ContinuousA>,
    nestedAssignmentDao: AssignmentDao<NestedA>,
    nestedAssignmentRefresher: AssignmentRefresher<NestedA>
) : SuperAssignmentRefresher<NestedA, ContinuousA>(
    assignmentDao = assignmentDao,
    nestedAssignmentDao = nestedAssignmentDao,
    nestedAssignmentRefresher = nestedAssignmentRefresher
)
