package ru.pashkovske.buratino.assignment.base.model.scheduling.properties

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = PeriodicAssignmentSchedulingProperties::class, name = "PERIODIC")
)
sealed class AssignmentSchedulingProperties(
    val type: AssignmentSchedulingType
)
