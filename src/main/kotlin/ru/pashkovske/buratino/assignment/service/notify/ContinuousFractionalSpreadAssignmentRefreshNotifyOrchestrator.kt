package ru.pashkovske.buratino.assignment.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.model.core.ContinuousFractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class ContinuousFractionalSpreadAssignmentRefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    refresher: AssignmentRefresher<ContinuousFractionalSpreadAssignment>
): RefreshNotifyOrchestrator<ContinuousFractionalSpreadAssignment>(
    taskScheduler = taskScheduler,
    refresher = refresher
)
