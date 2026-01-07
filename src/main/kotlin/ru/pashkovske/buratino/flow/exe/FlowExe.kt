package ru.pashkovske.buratino.flow.exe

import mu.KLogger
import mu.KotlinLogging
import ru.pashkovske.buratino.flow.exception.ExeFlowException
import ru.pashkovske.buratino.flow.exe.action.ActionRegistry
import ru.pashkovske.buratino.flow.exe.context.ExeCtx
import ru.pashkovske.buratino.flow.exe.router.Route
import ru.pashkovske.buratino.flow.exe.router.RouterRegistry
import ru.pashkovske.buratino.flow.model.Flow
import ru.pashkovske.buratino.flow.model.nodes.EndNode
import ru.pashkovske.buratino.flow.model.nodes.ExeNode
import ru.pashkovske.buratino.flow.model.nodes.Node
import ru.pashkovske.buratino.flow.model.nodes.RouteNode
import ru.pashkovske.buratino.flow.model.nodes.StartNode

abstract class FlowExe<Ctx: ExeCtx> {

    private val log: KLogger = KotlinLogging.logger {}

    lateinit var flow: Flow
    lateinit var actionRegistry: ActionRegistry<Ctx>
    lateinit var routerRegistry: RouterRegistry<Ctx>

    fun execute(ctx: Ctx): Ctx {
        log.info("Starting flow ${flow.name}")
        var node: Node = flow[flow.start]!!

        while (node !is EndNode) {
            node = when (node) {
                is StartNode -> executeStart(node)
                is ExeNode -> executeAction(
                    ctx = ctx,
                    action = node
                )
                is RouteNode -> executeRouter(
                    ctxView = ctx,
                    router = node
                )
                is Flow -> throw NotImplementedError()
                else -> throw ExeFlowException(
                    message = "Unknown node type ${node::class.simpleName}",
                    name = flow.name,
                    nodeName = node.name,
                    nodeType = node::class.simpleName ?: node::class.java.simpleName
                )
            }
        }

        return ctx
    }

    abstract fun build(
        flow: Flow,
        actionRegistry: ActionRegistry<Ctx>,
        routerRegistry: RouterRegistry<Ctx>
    ): FlowExe<Ctx>

    private fun executeStart(
        start: StartNode
    ): Node {
        val nextNode: Node = flow[start.next]!!
        return nextNode
    }

    private fun executeAction(
        ctx: Ctx,
        action: ExeNode
    ): Node {
        val actionSlug: String = action.action
        if (actionSlug !in actionRegistry) {
            throw ExeFlowException(
                message = "Action $actionSlug not found",
                name = flow.name,
                nodeName = action.name,
                nodeType = "ExeNode"
            )
        }
        actionRegistry[actionSlug]!!.execute(ctx)
        val nextNode: Node = flow[action.next]!!
        return nextNode
    }

    private fun executeRouter(
        ctxView: Ctx,
        router: RouteNode
    ): Node {
        val routerSlug: String = router.router
        if (routerSlug !in routerRegistry) {
            throw ExeFlowException(
                message = "Router $routerSlug not found",
                name = flow.name,
                nodeName = router.name,
                nodeType = "RouteNode"
            )
        }
        val route: Route = routerRegistry[routerSlug]!!.execute(ctxView)
        val nextNode: Node = flow[router[route.value]!!]!!
        return nextNode
    }
}
