package ru.pashkovske.buratino.flow.builder

import ru.pashkovske.buratino.flow.model.nodes.StartNode
import ru.pashkovske.buratino.flow.exception.BuildNodeException
import ru.pashkovske.buratino.flow.exception.FlowIsNotReadyException
import ru.pashkovske.buratino.flow.readiness.BuilderReadiness
import ru.pashkovske.buratino.flow.readiness.NodeNotReadyMessage
import java.util.UUID

class StartNodeBuilder : NodeBuilder(
    name = "Start"
) {
    override fun build(): StartNode {
        val readiness = validateUndefinedEdges()
        if (!readiness.isReady) {
            throw FlowIsNotReadyException(readiness.issues)
        }
        
        return StartNode(
            id = id,
            next = nextNode!!
        )
    }
    
    override fun validateUndefinedEdges(): BuilderReadiness {
        val issues = mutableListOf<NodeNotReadyMessage>()
        if (nextNode == null) {
            issues.add(
                NodeNotReadyMessage(
                    message = "Next node is not set",
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

    override fun setPrevious(previous: UUID): NodeBuilder {
        throw BuildNodeException(
            message = "StartNode cannot have a previous node",
            type = this::class,
            name = name,
            id = id
        )
    }
}