package ru.pashkovske.buratino.assignment.base.service.flow

import ru.pashkovske.buratino.assignment.base.model.flow.Flow
import ru.pashkovske.buratino.assignment.base.model.flow.nodes.Node
import ru.pashkovske.buratino.assignment.base.service.flow.builder.NodeBuilder
import ru.pashkovske.buratino.assignment.base.service.flow.builder.RouteNodeBuilder
import ru.pashkovske.buratino.assignment.base.service.flow.builder.StartNodeBuilder
import ru.pashkovske.buratino.assignment.base.service.flow.exception.BuildNodeException
import ru.pashkovske.buratino.assignment.base.service.flow.exception.FlowIsNotReadyException
import ru.pashkovske.buratino.assignment.base.service.flow.readiness.BuilderReadiness
import ru.pashkovske.buratino.assignment.base.service.flow.readiness.NotReadyMessage
import java.util.UUID

class FlowBuilder(
    name: String
): NodeBuilder(
    name = name
) {
    private val nodes: MutableMap<UUID, NodeBuilder> = mutableMapOf()
    private val start: UUID

    init {
        val startNodeBuilder = StartNodeBuilder()
        nodes[startNodeBuilder.id] = startNodeBuilder
        start = startNodeBuilder.id
    }

    fun getStart(): UUID {
        return start
    }

    operator fun get(id: UUID): NodeBuilder? {
        return nodes[id]
    }

    override fun build(): Flow {
        val readiness: BuilderReadiness = validateUndefinedEdges()
        if (!readiness.isReady) {
            throw FlowIsNotReadyException(readiness.issues)
        }

        val finalNodes: Map<UUID, Node> = nodes.entries
            .associate { (nodeId: UUID, node: NodeBuilder) ->
                nodeId to node.build()
            }
        return Flow(
            name = name,
            id = id,
            start = start,
            nodes = finalNodes
        )
    }

    override fun validateUndefinedEdges(): BuilderReadiness {
        val issues: List<NotReadyMessage> = nodes.values
            .map(NodeBuilder::validateUndefinedEdges)
            .flatMap(BuilderReadiness::issues)
        return BuilderReadiness(
            issues = issues,
            isReady = issues.isEmpty()
        )
    }

    fun addNodeToRoute(
        node: NodeBuilder,
        after: UUID,
        before: UUID? = null,
        route: String
    ): FlowBuilder {
        node.setPrevious(after)
        if (before != null) {
            node.setNext(before)
        }
        addNode(node, route)
        return this
    }

    fun addNode(
        node: NodeBuilder,
        after: UUID? = null,
        before: UUID? = null
    ): FlowBuilder {
        if (after != null) {
            node.setPrevious(after)
        }
        if (before != null) {
            node.setNext(before)
        }
        addNode(node)
        return this
    }

    private fun addNode(
        node: NodeBuilder,
        route: String? = null
    ) {
        if (node.id in nodes) {
            throwWithMessage("Cannot add node with id ${node.id} is already exist")
        }
        if (node is StartNodeBuilder) {
            throwWithMessage("Cannot add node with id ${node.id}, only one start node is allowed")
        }

        val previousId: UUID? = node.getPrevious()
        if (previousId != null) {
            addLinkToPrevious(
                node = node,
                previousId = previousId,
                route = route
            )
        }

        val before: UUID? = node.getNext()
        if (before != null) {
            addLinkToNext(
                node = node,
                nextId = before
            )
        }

        nodes[node.id] = node
    }

    private fun addLinkToPrevious(
        node: NodeBuilder,
        previousId: UUID,
        route: String? = null
    ) {
        if (previousId !in nodes) {
            throwWithMessage("Previous node with id $previousId is not found")
        }
        when (val previousNode: NodeBuilder = nodes[previousId]!!) {
            is RouteNodeBuilder -> if (route != null) {
                previousNode.addRoute(route, node.id)
            } else {
                previousNode.setNext(node.id) // will produce corresponding exception
            }
            else -> previousNode.setNext(node.id)
        }
    }

    private fun addLinkToNext(
        node: NodeBuilder,
        nextId: UUID
    ) {
        if (nextId !in nodes) {
            throwWithMessage("Next node with id $nextId is not found")
        }
        val nextNode: NodeBuilder = nodes[nextId]!!
        nextNode.setPrevious(node.id)
    }

    private fun throwWithMessage(message: String) {
        throw BuildNodeException(
            message = message,
            type = this::class,
            name = name,
            id = id
        )
    }
}