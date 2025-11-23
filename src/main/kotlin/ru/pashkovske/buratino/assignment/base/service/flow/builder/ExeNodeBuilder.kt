package ru.pashkovske.buratino.assignment.base.service.flow.builder

import ru.pashkovske.buratino.assignment.base.model.flow.nodes.ExeNode
import ru.pashkovske.buratino.assignment.base.service.flow.exception.FlowIsNotReadyException
import ru.pashkovske.buratino.assignment.base.service.flow.readiness.BuilderReadiness
import ru.pashkovske.buratino.assignment.base.service.flow.readiness.NodeNotReadyMessage

class ExeNodeBuilder(
    name: String,
    val action: String
): NodeBuilder(
    name = name
) {
    override fun build(): ExeNode {
        val readiness = validateUndefinedEdges()
        if (!readiness.isReady) {
            throw FlowIsNotReadyException(readiness.issues)
        }
        
        return ExeNode(
            name = name,
            id = id,
            action = action,
            next = next!!,
            previous = previous!!
        )
    }
    
    override fun validateUndefinedEdges(): BuilderReadiness {
        val issues = mutableListOf<NodeNotReadyMessage>()
        if (next == null) {
            issues.add(
                NodeNotReadyMessage(
                    message = "Next node is not set",
                    type = this::class,
                    name = name,
                    id = id
                )
            )
        }
        if (previous == null) {
            issues.add(
                NodeNotReadyMessage(
                    message = "Previous node is not set",
                    type = this::class,
                    name = name,
                    id = id
                )
            )
        }
        return BuilderReadiness(
            issues = issues,
            isReady = issues.isEmpty()
        )
    }
}
