package ru.pashkovske.buratino.assignment.base.model.flow.nodes

import java.util.UUID

class StartNode(
    id: UUID,
    val next: UUID
): Node(
    name = "Start",
    id = id
)
