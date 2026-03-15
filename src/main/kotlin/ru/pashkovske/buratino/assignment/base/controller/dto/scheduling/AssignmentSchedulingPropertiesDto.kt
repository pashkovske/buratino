package ru.pashkovske.buratino.assignment.base.controller.dto.scheduling

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = PeriodicAssignmentSchedulingPropertiesDto::class, name = "PERIODIC")
)
sealed class AssignmentSchedulingPropertiesDto(
    val type: AssignmentSchedulingType
)
