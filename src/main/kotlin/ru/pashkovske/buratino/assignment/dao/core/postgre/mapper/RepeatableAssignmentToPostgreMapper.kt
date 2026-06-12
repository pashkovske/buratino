package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.RepeatableAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment

abstract class RepeatableAssignmentToPostgreMapper<
    ChildA : Assignment,
    RepeatableA : RepeatableAssignment<ChildA>,
    RepeatableRow : RepeatableAssignmentPostgreRow<ChildA, RepeatableA>
    > : ParentAssignmentToPostgreMapper<
    ChildA,
    RepeatableA,
    RepeatableRow
    >()
