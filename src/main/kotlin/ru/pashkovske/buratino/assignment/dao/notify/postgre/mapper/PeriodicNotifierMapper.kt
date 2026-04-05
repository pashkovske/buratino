package ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicNotifierRow
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicNotifierProperties
import java.time.Duration

abstract class PeriodicNotifierMapper<Row : PeriodicNotifierRow> {

    fun toNotifier(row: Row): PeriodicAssignmentNotifier {
        return PeriodicAssignmentNotifier(
            id = row.id,
            assignmentId = row.assignmentId,
            taskId = row.taskId,
            state = row.state,
            properties = PeriodicNotifierProperties(
                period = Duration.ofNanos(row.periodNanos)
            )
        )
    }

    abstract fun toRow(notifier: PeriodicAssignmentNotifier): Row
}
