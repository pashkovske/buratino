package ru.pashkovske.buratino.assignment.base.scheduling

import java.util.UUID
import java.util.function.Consumer

class SchedulingAssignmentTask(
    private val action: Consumer<UUID>,
    private val assignmentId: UUID
): Runnable {

    override fun run() {
        action.accept(assignmentId)
    }
}
