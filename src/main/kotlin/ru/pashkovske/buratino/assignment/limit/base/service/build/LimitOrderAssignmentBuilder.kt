package ru.pashkovske.buratino.assignment.limit.base.service.build

import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignmentStartCmd

abstract class LimitOrderAssignmentBuilder<
    LimitA : LimitOrderAssignment,
    Cmd : LimitOrderAssignmentStartCmd<LimitA>
    > : AssignmentBuilder<LimitA, Cmd>
