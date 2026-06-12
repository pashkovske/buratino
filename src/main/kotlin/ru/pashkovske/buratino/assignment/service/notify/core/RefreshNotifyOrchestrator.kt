package ru.pashkovske.buratino.assignment.service.notify.core

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.common.scheduler.TaskScheduler

@Service
class RefreshNotifyOrchestrator(
    taskScheduler: TaskScheduler,
    notifierDao: RefreshNotifierDao
) : BasicNotifyOrchestrator(
    taskScheduler = taskScheduler,
    notifierDao = notifierDao
)
