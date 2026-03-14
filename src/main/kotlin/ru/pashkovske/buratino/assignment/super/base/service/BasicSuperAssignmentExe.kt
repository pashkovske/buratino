package ru.pashkovske.buratino.assignment.`super`.base.service

import ru.pashkovske.buratino.assignment.base.service.AssignmentExe
import ru.pashkovske.buratino.assignment.base.service.BasicAssignmentExe
import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.dao.AssignmentDao
import ru.pashkovske.buratino.assignment.base.service.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.base.service.start.AssignmentStarter
import ru.pashkovske.buratino.common.scheduler.TaskScheduler
import ru.pashkovske.buratino.assignment.`super`.base.model.SuperAssignment

abstract class BasicSuperAssignmentExe<
    SuperA : SuperAssignment<Nested>,
    Nested : Assignment
    >(
    assignmentDao: AssignmentDao<SuperA>,
    taskScheduler: TaskScheduler,
    assignmentRefresher: AssignmentRefresher<SuperA>,
    assignmentCanceller: AssignmentCanceller<SuperA>,
    assignmentStarter: AssignmentStarter<SuperA>,
    protected val nestedAssignmentExe: AssignmentExe<Nested>,
    protected val nestedAssignmentDao: AssignmentDao<Nested>
) : BasicAssignmentExe<SuperA>(
    assignmentDao = assignmentDao,
    taskScheduler = taskScheduler,
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter
)
