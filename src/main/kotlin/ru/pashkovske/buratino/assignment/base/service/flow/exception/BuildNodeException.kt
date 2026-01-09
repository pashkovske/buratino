package ru.pashkovske.buratino.assignment.base.service.flow.exception

import ru.pashkovske.buratino.assignment.base.service.flow.builder.NodeBuilder
import java.util.UUID
import kotlin.reflect.KClass

class BuildNodeException(
    message: String,
    type: KClass<out NodeBuilder>,
    name: String,
    id: UUID
): BuildFlowException(message = """
    Error while building node:
        Node type: ${type.simpleName}
        Node name: $name
        Node id: $id
    Message:
    $message
""".trimIndent())
