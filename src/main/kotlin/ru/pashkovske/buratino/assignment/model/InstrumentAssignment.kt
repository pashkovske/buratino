package ru.pashkovske.buratino.assignment.model

import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class InstrumentAssignment(
    open val id: UUID = UUID.randomUUID(),
    open val iid: InstrumentId,
    open var status: AssignmentStatus = AssignmentStatus.QUEUED
)
