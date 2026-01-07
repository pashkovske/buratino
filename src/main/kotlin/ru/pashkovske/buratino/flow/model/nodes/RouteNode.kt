package ru.pashkovske.buratino.flow.model.nodes

import ru.pashkovske.buratino.flow.exception.BuildFlowException
import java.util.UUID

class RouteNode(
    name: String,
    id: UUID,
    val previous: UUID,
    val router: String,
    val routes: Map<String, UUID>
): Node(
    name = name,
    id = id
) {
    init {
        if(routes.values.toSet().size < routes.size) {
            throw BuildFlowException("Routes must be unique, in node $this they are not")
        }
    }

    operator fun get(route: String): UUID? = routes[route]
}
