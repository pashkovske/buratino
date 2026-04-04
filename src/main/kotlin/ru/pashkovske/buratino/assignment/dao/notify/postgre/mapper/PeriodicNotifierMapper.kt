package ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicNotifierRow
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties
import java.time.Duration

abstract class PeriodicNotifierMapper<Row : PeriodicNotifierRow> {

    fun toNotifier(row: Row): PeriodicAssignmentScheduling {
        return PeriodicAssignmentScheduling(
            id = row.id,
            assignmentId = row.assignmentId,
            taskId = row.taskId,
            state = row.state,
            properties = PeriodicAssignmentSchedulingProperties(
                period = Duration.ofNanos(row.periodNanos)
            )
        )
    }

    abstract fun toRow(notifier: PeriodicAssignmentScheduling): Row
}
