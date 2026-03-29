package ru.pashkovske.buratino.assignment.model.notify.properties

import java.time.Duration

class PeriodicAssignmentSchedulingProperties(
    val period: Duration
) : AssignmentSchedulingProperties()
