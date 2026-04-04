package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.LimitOrderAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.service.core.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter

abstract class LimitOrderAssignmentExe<
    LimitA : LimitOrderAssignment,
    LimitCmd : LimitOrderAssignmentStartCmd<LimitA>
    >(
    assignmentDao: AssignmentDao<LimitA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator,
    assignmentRefresher: AssignmentRefresher<LimitA>,
    assignmentCanceller: AssignmentCanceller<LimitA>,
    assignmentStarter: AssignmentStarter<LimitA>,
    assignmentBuilder: AssignmentBuilder<LimitA, LimitCmd>
) : BasicAssignmentExe<LimitA, LimitCmd>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder
)