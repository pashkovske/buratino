package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ParentAssignmentStartCmd<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment
    >(
    iid: InstrumentId,
    refreshNotifierProperties: NotifierProperties?
) : AssignmentStartCmd<ParentA>(
    iid = iid,
    refreshNotifierProperties = refreshNotifierProperties
)