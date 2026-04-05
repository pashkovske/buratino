package ru.pashkovske.buratino.assignment.model.notify.properties

import java.time.Duration

class PeriodicNotifierProperties(
    val period: Duration
) : NotifierProperties()
