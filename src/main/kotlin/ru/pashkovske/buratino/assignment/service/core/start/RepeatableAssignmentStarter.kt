package ru.pashkovske.buratino.assignment.service.core.start

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment

abstract class RepeatableAssignmentStarter<
    RepeatableA : RepeatableAssignment<ChildA>,
    ChildA : Assignment
    >(
    assignmentDao: AssignmentDao<RepeatableA>
) : ParentAssignmentStarter<RepeatableA, ChildA>(
    assignmentDao = assignmentDao
)
