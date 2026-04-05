package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentNotifierDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.NotifierPropertiesDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.PeriodicNotifierPropertiesDto
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.properties.NotifierProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicNotifierProperties

object AssignmentNotifierMapper {

    fun toDto(properties: NotifierProperties?): NotifierPropertiesDto? {
        return when (properties) {
            is PeriodicNotifierProperties -> PeriodicNotifierPropertiesDto(
                period = properties.period
            )
            null -> null
        }
    }

    fun toDto(notifier: AssignmentNotifier?): AssignmentNotifierDto? {
        return notifier?.let {
            AssignmentNotifierDto(
                id = it.id,
                properties = toDto(it.properties),
                taskId = it.taskId,
                state = it.state
            )
        }
    }
}