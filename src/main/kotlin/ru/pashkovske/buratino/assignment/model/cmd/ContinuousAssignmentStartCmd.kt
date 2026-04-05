package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ContinuousAssignmentStartCmd<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment
    >(
    iid: InstrumentId,
    val continueNotifierProperties: NotifierProperties?,
    refreshNotifierProperties: NotifierProperties?
) : ParentAssignmentStartCmd<ContinuousA, ChildA>(
    iid = iid,
    refreshNotifierProperties = refreshNotifierProperties
)