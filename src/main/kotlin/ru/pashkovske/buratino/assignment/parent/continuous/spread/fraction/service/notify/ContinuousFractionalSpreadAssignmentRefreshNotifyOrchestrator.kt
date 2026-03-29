package ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.parent.continuous.spread.fraction.model.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class ContinuousFractionalSpreadAssignmentRefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    refresher: AssignmentRefresher<ContinuousFractionalSpreadAssignment>
): RefreshNotifyOrchestrator<ContinuousFractionalSpreadAssignment>(
    taskScheduler = taskScheduler,
    refresher = refresher
)
