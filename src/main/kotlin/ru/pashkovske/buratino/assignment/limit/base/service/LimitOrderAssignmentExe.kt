package ru.pashkovske.buratino.assignment.limit.base.service

import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.service.build.AssignmentBuilder
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignmentStartCmd

abstract class LimitOrderAssignmentExe<
    LimitA : LimitOrderAssignment,
    LimitCmd : LimitOrderAssignmentStartCmd<LimitA>
    >(
    assignmentDao: AssignmentDao<LimitA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<LimitA>,
    assignmentCanceller: AssignmentCanceller<LimitA>,
    assignmentStarter: AssignmentStarter<LimitA>,
    assignmentBuilder: AssignmentBuilder<LimitA, LimitCmd>
): BasicAssignmentExe<LimitA, LimitCmd>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder
)
