package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ParentAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ParentAssignmentStartCmd<
    ParentA : ParentAssignment<ChildA>,
    ChildA : Assignment
    >(
    iid: InstrumentId,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
) : AssignmentStartCmd<ParentA>(
    iid = iid,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties
)