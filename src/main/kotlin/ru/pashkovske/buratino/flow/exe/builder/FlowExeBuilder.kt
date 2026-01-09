package ru.pashkovske.buratino.flow.exe.builder

import ru.pashkovske.buratino.flow.builder.FlowBuilder
import ru.pashkovske.buratino.flow.builder.ExeNodeBuilder
import ru.pashkovske.buratino.flow.builder.RouteNodeBuilder
import ru.pashkovske.buratino.flow.exe.FlowExe
import ru.pashkovske.buratino.flow.exe.action.ActionExe
import ru.pashkovske.buratino.flow.exe.action.ActionRegistry
import ru.pashkovske.buratino.flow.exe.router.Route
import ru.pashkovske.buratino.flow.exe.router.RouterExe
import ru.pashkovske.buratino.flow.exe.router.RouterRegistry
import java.util.UUID

abstract class FlowExeBuilder<T>(
    flowName: String
) {

    protected val flowBuilder = FlowBuilder(flowName)
    protected val actionRegistry: ActionRegistry<T> = ActionRegistry()
    protected val routerRegistry: RouterRegistry<T> = RouterRegistry()

    fun buildFlow(emptyFlowExe: FlowExe<T>): FlowExe<T> {
        return emptyFlowExe.build(
            flow = flowBuilder.build(),
            actionRegistry = actionRegistry,
            routerRegistry = routerRegistry
        )
    }

    private fun buildExeNode(action: ActionExe<T>): ExeNodeBuilder {
        actionRegistry.registerAction(action)
        flowBuilder.registerAction(action.name)
        return ExeNodeBuilder(
            name = action.name,
            action = action.name
        )
    }

    protected fun addAction(
        action: ActionExe<T>,
        afterNode: UUID
    ): UUID {
        val exeNode = buildExeNode(action)
        flowBuilder.addNode(
            after = afterNode,
            node = exeNode
        )
        return exeNode.id
    }

    protected fun addAction(
        action: ActionExe<T>,
        afterNode: UUID,
        route: Route
    ): UUID {
        val exeNode = buildExeNode(action)
        flowBuilder.addNodeToRoute(
            after = afterNode,
            node = exeNode,
            route = route.value
        )
        return exeNode.id
    }

    protected fun addRouter(
        router: RouterExe<T>,
        afterNode: UUID
    ): UUID {
        routerRegistry.registerRouter(router)
        flowBuilder.registerRouter(
            router = router.name,
            routes = router.defaultRoute.allRoutes
        )
        val routeNode = RouteNodeBuilder(
            name = router.name,
            router = router.name,
            routeNames = router.defaultRoute.allRoutes
        )
        flowBuilder.addNode(
            after = afterNode,
            node = routeNode
        )
        return routeNode.id
    }
}