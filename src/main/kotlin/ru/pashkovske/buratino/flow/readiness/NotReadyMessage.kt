package ru.pashkovske.buratino.flow.readiness

open class NotReadyMessage(
    val message: String
) {
    override fun toString(): String {
        return message
    }
}
