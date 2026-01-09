package ru.pashkovske.buratino.flow.builder

import ru.pashkovske.buratino.flow.model.nodes.RouteNode
import ru.pashkovske.buratino.flow.exception.BuildNodeException
import ru.pashkovske.buratino.flow.exception.FlowIsNotReadyException
import ru.pashkovske.buratino.flow.readiness.BuilderReadiness
import ru.pashkovske.buratino.flow.readiness.NodeNotReadyMessage
import java.util.UUID

class RouteNodeBuilder(
    name: String,
    routeNames: Set<String>,
    val router: String
) : NodeBuilder(
    name = name
) {
    private val routes: MutableMap<String, UUID?> = mutableMapOf()

    init {
        routeNames.forEach { route ->
            routes[route] = null
        }
    }

    fun addRoute(route: String, next: UUID): RouteNodeBuilder {
        if (route !in routes) {
            throw BuildNodeException(
                message = "Trying to add route $route, but it is not in the list of routes:\n${routes.keys.joinToString("\t\n")}",
                type = this::class,
                name = name,
                id = id
            )
        }
        if (routes[route] != null) {
            throw BuildNodeException(
                message = "Route $route is already added with next node ${routes[route]}",
                type = this::class,
                name = name,
                id = id
            )
        }
        routes[route] = next
        return this
    }

    override fun build(): RouteNode {
        val readiness = validateUndefinedEdges()
        if (!readiness.isReady) {
            throw FlowIsNotReadyException(readiness.issues)
        }
        
        return RouteNode(
            name = name,
            id = id,
            previous = previousNode!!,
            router = router,
            routes = routes.mapValues { it.value!! }
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
        routes
            .filter { it.value == null }
            .map(Map.Entry<String, UUID?>::key)
            .forEach { key: String ->
                issues.add(
                    NodeNotReadyMessage(
                        message = "Route $key is not set",
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
            message = "RouteNode cannot have a next node, use addRoute instead",
            type = this::class,
            name = name,
            id = id
        )
    }
}
