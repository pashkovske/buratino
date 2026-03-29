package ru.pashkovske.buratino.assignment.limit.top.price.service.notify

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.service.notify.RefreshNotifyOrchestrator
import ru.pashkovske.buratino.assignment.base.service.refresh.AssignmentRefresher
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class TopPriceAssignmentRefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    refresher: AssignmentRefresher<TopPriceAssignment>
): RefreshNotifyOrchestrator<TopPriceAssignment>(
    taskScheduler = taskScheduler,
    refresher = refresher
)
