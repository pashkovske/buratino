package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.RepeatableAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class RepeatableAssignmentStartCmd<
    RepeatableA : RepeatableAssignment<ChildA>,
    ChildA : Assignment
    >(
    iid: InstrumentId,
    refreshNotifierProperties: NotifierProperties?
) : ParentAssignmentStartCmd<RepeatableA, ChildA>(
    iid = iid,
    refreshNotifierProperties = refreshNotifierProperties
)
