package ru.pashkovske.buratino.assignment.service.core.build

import ru.pashkovske.buratino.assignment.dao.core.AssignmentDao
import ru.pashkovske.buratino.assignment.model.cmd.LimitOrderAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

abstract class LimitOrderAssignmentBuilder<
    LimitA : LimitOrderAssignment,
    Cmd : LimitOrderAssignmentStartCmd<LimitA>
    >(
    assignmentDao: AssignmentDao<LimitA>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : BasicAssignmentBuilder<LimitA, Cmd>(
    assignmentDao = assignmentDao,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)