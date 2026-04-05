package ru.pashkovske.buratino.assignment.controller.dto.notify

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = PeriodicNotifierPropertiesDto::class, name = "PERIODIC")
)
sealed class NotifierPropertiesDto(
    val type: AssignmentNotifierType
)
