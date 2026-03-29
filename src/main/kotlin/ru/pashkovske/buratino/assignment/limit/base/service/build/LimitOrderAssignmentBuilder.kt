package ru.pashkovske.buratino.assignment.limit.base.service.build

import ru.pashkovske.buratino.assignment.base.service.build.BasicAssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignmentStartCmd

abstract class LimitOrderAssignmentBuilder<
    LimitA : LimitOrderAssignment,
    Cmd : LimitOrderAssignmentStartCmd<LimitA>
    >(
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<LimitA>
) : BasicAssignmentBuilder<LimitA, Cmd>(
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)
