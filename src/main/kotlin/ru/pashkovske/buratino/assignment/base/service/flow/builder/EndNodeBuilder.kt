package ru.pashkovske.buratino.assignment.base.service.flow.builder

import ru.pashkovske.buratino.assignment.base.model.flow.nodes.EndNode
import ru.pashkovske.buratino.assignment.base.service.flow.exception.BuildNodeException
import ru.pashkovske.buratino.assignment.base.service.flow.exception.FlowIsNotReadyException
import ru.pashkovske.buratino.assignment.base.service.flow.readiness.BuilderReadiness
import ru.pashkovske.buratino.assignment.base.service.flow.readiness.NodeNotReadyMessage
import java.util.UUID

class EndNodeBuilder(
    name: String,
    val resolution: String
): NodeBuilder(
    name = name
) {
    override fun build(): EndNode {
        val readiness = validateUndefinedEdges()
        if (!readiness.isReady) {
            throw FlowIsNotReadyException(readiness.issues)
        }
        
        return EndNode(
            name = name,
            id = id,
            previous = previousNode!!
        )
    }
    
    override fun validateUndefinedEdges(): BuilderReadiness {
        val issues = mutableListOf<NodeNotReadyMessage>()
        if (previousNode == null) {
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
    
    override fun setNext(next: UUID): NodeBuilder {
        throw BuildNodeException(
            message = "EndNode cannot have a next node $next",
            type = this::class,
            name = name,
            id = id
        )
    }
}
