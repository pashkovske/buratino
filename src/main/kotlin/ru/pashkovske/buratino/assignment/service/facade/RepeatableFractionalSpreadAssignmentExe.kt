package ru.pashkovske.buratino.assignment.service.facade

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.cmd.FractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.cmd.RepeatableFractionalSpreadAssignmentStartCmd
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableFractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.core.build.RepeatableFractionalSpreadAssignmentBuilder
import ru.pashkovske.buratino.assignment.service.core.cancel.AssignmentCanceller
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.service.core.start.AssignmentStarter
import ru.pashkovske.buratino.assignment.service.notify.core.RefreshNotifyOrchestrator

@Service
final class RepeatableFractionalSpreadAssignmentExe(
    assignmentRefresher: AssignmentRefresher<RepeatableFractionalSpreadAssignment>,
    assignmentCanceller: AssignmentCanceller<RepeatableFractionalSpreadAssignment>,
    assignmentStarter: AssignmentStarter<RepeatableFractionalSpreadAssignment>,
    assignmentBuilder: RepeatableFractionalSpreadAssignmentBuilder,
    refreshNotifyOrchestrator: RefreshNotifyOrchestrator
) : BasicRepeatableAssignmentExe<
    RepeatableFractionalSpreadAssignment,
    FractionalSpreadAssignment,
    RepeatableFractionalSpreadAssignmentStartCmd,
    FractionalSpreadAssignmentStartCmd
    >(
    assignmentRefresher = assignmentRefresher,
    assignmentCanceller = assignmentCanceller,
    assignmentStarter = assignmentStarter,
    assignmentBuilder = assignmentBuilder,
    refreshNotifyOrchestrator = refreshNotifyOrchestrator
)
