package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.model.cmd.LimitOrderAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.service.core.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

abstract class LimitOrderAssignmentExe<
    LimitA : LimitOrderAssignment,
    LimitCmd : LimitOrderAssignmentStartCmd<LimitA>
    >(
    assignmentRefresher: AssignmentRefresher<LimitA>,
    assignmentCanceller: AssignmentCanceller<LimitA>,
    assignmentStarter: AssignmentStarter<LimitA>,
    assignmentBuilder: AssignmentBuilder<LimitA, LimitCmd>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : BasicAssignmentExe<LimitA, LimitCmd>(
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)