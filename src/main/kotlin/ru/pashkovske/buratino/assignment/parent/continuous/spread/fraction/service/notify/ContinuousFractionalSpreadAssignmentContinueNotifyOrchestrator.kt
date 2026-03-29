package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.`continue`.ContinuousAssignmentContinuer
import ru.pashkovske.buratino.assignment.parent.continuous.base.service.notify.ContinueNotifyOrchestrator
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
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
