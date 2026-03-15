package ru.pashkovske.buratino.assignment.parent.continuous.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.parent.base.model.ParentAssignmentStartCmd
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class ContinuousAssignmentStartCmd<
    ContinuousA : ContinuousAssignment<ChildA>,
    ChildA : Assignment
    >(
    iid: InstrumentId,
    val continueAssignmentSchedulingProperties: AssignmentSchedulingProperties?,
    refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
) : ParentAssignmentStartCmd<ContinuousA, ChildA>(
    iid = iid,
    refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties
)
