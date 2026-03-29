package ru.pashkovske.buratino.assignment.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.core.continuation.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class ContinuousFractionalSpreadAssignmentContinueNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    continuer: ContinuousAssignmentContinuer<ContinuousFractionalSpreadAssignment>
): ContinueNotifyOrchestrator<
    ContinuousFractionalSpreadAssignment,
    FractionalSpreadAssignment
    >(
    taskScheduler = taskScheduler,
    continuer = continuer
)
