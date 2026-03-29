package ru.pashkovske.buratino.assignment.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.service.core.refresh.AssignmentRefresher
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class TopPriceAssignmentRefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    refresher: AssignmentRefresher<TopPriceAssignment>
): RefreshNotifyOrchestrator<TopPriceAssignment>(
    taskScheduler = taskScheduler,
    refresher = refresher
)