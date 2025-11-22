package ru.pashkovske.buratino.assignment.base.model.flow.nodes

import java.util.UUID

class EndNode(
    name: String,
    id: UUID,
    val previous: UUID
): Node(
    name = name,
    id = id
)
