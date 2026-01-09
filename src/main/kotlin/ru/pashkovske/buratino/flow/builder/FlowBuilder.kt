package ru.pashkovske.buratino.flow.builder

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.flow.exception.BuildNodeException
import ru.pashkovske.buratino.flow.exception.FlowIsNotReadyException
import ru.pashkovske.buratino.flow.model.Flow
import ru.pashkovske.buratino.flow.model.nodes.Node
import ru.pashkovske.buratino.flow.readiness.BuilderReadiness
import ru.pashkovske.buratino.flow.readiness.NotReadyMessage
import java.util.UUID

class FlowBuilder(
    name: String
): NodeBuilder(
    name = name
) {

    private val log: KLogger = KotlinLogging.logger {}
    private val nodes: MutableMap<UUID, NodeBuilder> = mutableMapOf()
    private val actions: MutableSet<String> = mutableSetOf()
    private val usedActions: MutableSet<String> = mutableSetOf()
    private val resolutions : MutableSet<String> = mutableSetOf()
    private val usedResolutions: MutableSet<String> = mutableSetOf()
    private val routers: MutableMap<String, Set<String>> = mutableMapOf()
    private val usedRouters: MutableSet<String> = mutableSetOf()
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

        if (usedActions.size != actions.size) {
            for (action: String in actions) {
                if (!usedActions.contains(action)) {
                    log.warn("Action $action is not used in flow $name")
                }
            }
        }
        if (usedResolutions.size != resolutions.size) {
            for (resolution: String in resolutions) {
                if (!usedResolutions.contains(resolution)) {
                    log.warn("Resolution $resolution is not used in flow $name")
                }
            }
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

    fun registerAction(action: String): FlowBuilder {
        if (action in actions) {
            log.warn("Action $action is already registered in flow $name")
            return this
        }
        actions.add(action)
        return this
    }

    fun registerResolution(resolution: String): FlowBuilder {
        if (resolution in resolutions) {
            log.warn("Resolution $resolution is already registered in flow $name")
            return this
        }
        resolutions.add(resolution)
        return this
    }

    fun registerRouter(
        router: String,
        routes: Set<String>
    ): FlowBuilder {
        if (routes.isEmpty()) {
            throwWithMessage("Cannot register router $router with empty routes")
        }
        if (router in routers) {
            log.warn("Router $router is already registered in flow $name")
            if (routers[router]!! != routes) {
                throwWithMessage("Cannot rewrite routes of router $router. Different routes are already registered")
            }
            return this
        }
        routers[router] = routes
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

        when (node) {
            is ExeNodeBuilder -> addExeNode(node)
            is EndNodeBuilder -> addEndNode(node)
            is RouteNodeBuilder -> addRouteNode(node)
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

    private fun addExeNode(node: ExeNodeBuilder) {
        if (node.action !in actions) {
            throwWithMessage("Action ${node.action} of node ${node.id} with name ${node.name} is not registered")
        }
        usedActions.add(node.action)
    }

    private fun addEndNode(node: EndNodeBuilder) {
        if (node.resolution !in resolutions) {
            throwWithMessage("Resolution ${node.resolution} of node ${node.id} with name ${node.name} is not registered")
        }
        usedResolutions.add(node.resolution)
    }

    private fun addRouteNode(node: RouteNodeBuilder) {
        if (node.router !in routers) {
            throwWithMessage("Router ${node.router} of node ${node.id} with name ${node.name} is not registered")
        }
        usedRouters.add(node.router)
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