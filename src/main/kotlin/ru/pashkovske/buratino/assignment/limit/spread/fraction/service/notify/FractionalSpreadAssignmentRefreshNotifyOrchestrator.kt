package ru.pashkovske.buratino.assignment.limit.spread.fraction.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class FractionalSpreadAssignmentRefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    refresher: AssignmentRefresher<FractionalSpreadAssignment>
): RefreshNotifyOrchestrator<FractionalSpreadAssignment>(
    taskScheduler = taskScheduler,
    refresher = refresher
)
