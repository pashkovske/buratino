package ru.pashkovske.buratino.assignment.dao.notify.postgre.row

import org.springframework.data.annotation.Id
import ru.pashkovske.buratino.assignment.model.notify.NotifierState
import java.util.UUID

abstract class PeriodicNotifierRow(
    @Id
    open val id: UUID,
    open val assignmentId: UUID,
    open val taskId: UUID?,
    open val state: NotifierState,
    open val periodNanos: Long
)
