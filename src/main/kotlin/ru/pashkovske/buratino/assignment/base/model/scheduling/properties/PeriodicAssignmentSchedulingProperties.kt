package ru.pashkovske.buratino.assignment.base.model.scheduling.properties

import java.time.Duration

class PeriodicAssignmentSchedulingProperties(
    val period: Duration
) : AssignmentSchedulingProperties()
