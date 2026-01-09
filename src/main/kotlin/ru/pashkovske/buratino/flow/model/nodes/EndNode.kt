package ru.pashkovske.buratino.flow.model.nodes

import java.util.UUID

class EndNode(
    name: String,
    id: UUID,
    val previous: UUID
): Node(
    name = name,
    id = id
)
