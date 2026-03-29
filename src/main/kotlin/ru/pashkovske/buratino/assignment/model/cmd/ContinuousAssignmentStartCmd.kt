package ru.pashkovske.buratino.assignment.model.cmd

import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
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