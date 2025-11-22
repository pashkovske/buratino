package ru.pashkovske.buratino.assignment.base.model.flow.nodes

import java.util.UUID

class ExeNode(
    name: String,
    id: UUID,
    val action: String,
    val previous: UUID,
    val next: UUID
): Node(
    name = name,
    id = id
)
