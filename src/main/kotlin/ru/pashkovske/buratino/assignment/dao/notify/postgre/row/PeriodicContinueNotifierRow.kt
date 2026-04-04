package ru.pashkovske.buratino.assignment.dao.notify.postgre.row

import org.springframework.data.relational.core.mapping.Table
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import java.util.UUID

@Table("notify.periodic_continue")
data class PeriodicContinueNotifierRow(
    override val id: UUID,
    override val assignmentId: UUID,
    override val taskId: UUID?,
    override val state: SchedulingState,
    override val periodNanos: Long
) : PeriodicNotifierRow(
    id = id,
    assignmentId = assignmentId,
    taskId = taskId,
    state = state,
    periodNanos = periodNanos
)
