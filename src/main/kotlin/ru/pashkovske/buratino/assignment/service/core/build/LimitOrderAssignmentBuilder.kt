package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.model.cmd.LimitOrderAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator

abstract class LimitOrderAssignmentBuilder<
    LimitA : LimitOrderAssignment,
    Cmd : LimitOrderAssignmentStartCmd<LimitA>
    >(
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator<LimitA>
) : BasicAssignmentBuilder<LimitA, Cmd>(
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)