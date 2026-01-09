package ru.pashkovske.buratino.flow.readiness

data class BuilderReadiness(
    val issues: List<NotReadyMessage>,
    val isReady: Boolean
)