package ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicRefreshNotifierRow
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling

@Component
class PeriodicRefreshNotifierMapper : PeriodicNotifierMapper<PeriodicRefreshNotifierRow>() {

    override fun toRow(notifier: PeriodicAssignmentScheduling): PeriodicRefreshNotifierRow {
        return PeriodicRefreshNotifierRow(
            id = notifier.id,
            assignmentId = notifier.assignmentId,
            taskId = notifier.taskId,
            state = notifier.state,
            periodNanos = notifier.properties.period.toNanos()
        )
    }
}
