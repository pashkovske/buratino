package ru.pashkovske.buratino.assignment.base.service.flow.readiness

data class BuilderReadiness(
    val issues: List<NotReadyMessage>,
    val isReady: Boolean
)