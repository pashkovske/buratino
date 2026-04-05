package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class AssignmentStartCmd<A : Assignment>(
    val iid: InstrumentId,
    val refreshNotifierProperties: NotifierProperties?
)