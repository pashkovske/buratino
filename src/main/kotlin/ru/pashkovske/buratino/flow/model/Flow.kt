package ru.pashkovske.buratino.flow.model

import ru.pashkovske.buratino.flow.model.nodes.Node
import java.util.UUID

class Flow(
    name: String,
    id: UUID,
    val start: UUID,
    val nodes: Map<UUID, Node>
): Node(
    name = name,
    id = id
) {
    operator fun get(id: UUID): Node? {
        return nodes[id]
    }
}
