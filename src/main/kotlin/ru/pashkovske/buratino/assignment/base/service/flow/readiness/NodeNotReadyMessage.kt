package ru.pashkovske.buratino.assignment.base.service.flow.readiness

import ru.pashkovske.buratino.assignment.base.service.flow.builder.NodeBuilder
import java.util.UUID
import kotlin.reflect.KClass

class NodeNotReadyMessage(
    message: String,
    type: KClass<out NodeBuilder>,
    name: String,
    id: UUID
): NotReadyMessage(message = """
    Error while building node:
        Node type: ${type.simpleName}
        Node name: $name
        Node id: $id
    Message:
    $message
""".trimIndent()) {
}