package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.controller.dto.notify.PeriodicAssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties

object AssignmentSchedulingMapper {

    fun toDto(properties: AssignmentSchedulingProperties?): AssignmentSchedulingPropertiesDto? {
        return when (properties) {
            is PeriodicAssignmentSchedulingProperties -> PeriodicAssignmentSchedulingPropertiesDto(
                period = properties.period
            )
            null -> null
        }
    }

    fun toDto(scheduling: AssignmentScheduling?): AssignmentSchedulingDto? {
        return scheduling?.let {
            AssignmentSchedulingDto(
                id = it.id,
                properties = toDto(it.properties),
                taskId = it.taskId,
                state = it.state
            )
        }
    }
}