package ru.pashkovske.buratino.assignment.parent.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.AssignmentStartCmd
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
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
