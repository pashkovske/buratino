package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ContinuousAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment

abstract class ContinuousAssignmentToPostgreMapper<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>,
    ContinuousRow : ContinuousAssignmentPostgreRow<ChildA, ContinuousA>
    > : ParentAssignmentToPostgreMapper<
    ChildA,
    ContinuousA,
    ContinuousRow
    >()
