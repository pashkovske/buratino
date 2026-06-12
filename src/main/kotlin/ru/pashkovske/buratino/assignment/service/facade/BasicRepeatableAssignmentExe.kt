package ru.pashkovske.buratino.assignment.service.facade

import ru.pashkovske.buratino.assignment.model.cmd.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.RepeatableAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment
import ru.pashkovske.buratino.assignment.service.core.build.RepeatableAssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

abstract class BasicRepeatableAssignmentExe<
    RepeatableA : RepeatableAssignment<ChildA>,
    ChildA : Assignment,
    RepeatableStartCmd : RepeatableAssignmentStartCmd<RepeatableA, ChildA>,
    ChildCmd : AssignmentStartCmd<ChildA>
    >(
    assignmentRefresher: AssignmentRefresher<RepeatableA>,
    assignmentCanceller: AssignmentCanceller<RepeatableA>,
    assignmentStarter: AssignmentStarter<RepeatableA>,
    assignmentBuilder: RepeatableAssignmentBuilder<RepeatableA, ChildA, RepeatableStartCmd, ChildCmd>,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) :
    ParentAssignmentExe<RepeatableA, ChildA, RepeatableStartCmd, ChildCmd>(
        assignmentRefresher = assignmentRefresher,
        assignmentCanceller = assignmentCanceller,
        assignmentStarter = assignmentStarter,
        assignmentBuilder = assignmentBuilder,
        refreshNotifyOrchestrator = refreshNotifyOrchestrator
    )
