package ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicContinueNotifierRow
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling

@Component
class PeriodicContinueNotifierMapper : PeriodicNotifierMapper<PeriodicContinueNotifierRow>() {

    override fun toRow(notifier: PeriodicAssignmentScheduling): PeriodicContinueNotifierRow {
        return PeriodicContinueNotifierRow(
            id = notifier.id,
            assignmentId = notifier.assignmentId,
            taskId = notifier.taskId,
            state = notifier.state,
            periodNanos = notifier.properties.period.toNanos()
        )
    }
}
