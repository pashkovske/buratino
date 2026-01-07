package ru.pashkovske.buratino.flow.model.nodes

import java.util.UUID

class StartNode(
    id: UUID,
    val next: UUID
): Node(
    name = "Start",
    id = id
)
