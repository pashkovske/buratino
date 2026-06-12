package ru.pashkovske.buratino.assignment.service.core.cancel

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment

abstract class RepeatableAssignmentCanceller<
    ChildA : Assignment,
    RepeatableA : RepeatableAssignment<ChildA>
    >(
    assignmentDao: AssignmentDao<RepeatableA>,
    childAssignmentCanceller: AssignmentCanceller<ChildA>
) : ParentAssignmentCanceller<RepeatableA, ChildA>(
    assignmentDao = assignmentDao,
    childAssignmentCanceller = childAssignmentCanceller
)
