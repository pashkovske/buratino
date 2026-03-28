package ru.pashkovske.buratino.assignment.base.controller.mapper

import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.AssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.controller.dto.scheduling.PeriodicAssignmentSchedulingPropertiesDto
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties

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
