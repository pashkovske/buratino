package ru.pashkovske.buratino.assignment.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class FractionalSpreadAssignmentRefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    refresher: AssignmentRefresher<FractionalSpreadAssignment>
): RefreshNotifyOrchestrator<FractionalSpreadAssignment>(
    taskScheduler = taskScheduler,
    refresher = refresher
)