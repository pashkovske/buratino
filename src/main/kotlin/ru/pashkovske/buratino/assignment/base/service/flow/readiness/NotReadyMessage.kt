package ru.pashkovske.buratino.assignment.base.service.flow.readiness

open class NotReadyMessage(
    val message: String
) {
    override fun toString(): String {
        return message
    }
}
