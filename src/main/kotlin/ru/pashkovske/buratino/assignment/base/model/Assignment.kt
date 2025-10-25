package ru.pashkovske.buratino.assignment.base.model

import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class Assignment(
    open val id: UUID = UUID.randomUUID(),
    open val iid: InstrumentId,
    open var status: AssignmentStatus = AssignmentStatus.QUEUED
)
