package ru.pashkovske.buratino.unit.assignment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.pashkovske.buratino.assignment.base.model.flow.Flow
import ru.pashkovske.buratino.assignment.base.model.flow.nodes.EndNode
import ru.pashkovske.buratino.assignment.base.model.flow.nodes.ExeNode
import ru.pashkovske.buratino.assignment.base.model.flow.nodes.Node
import ru.pashkovske.buratino.assignment.base.model.flow.nodes.RouteNode
import ru.pashkovske.buratino.assignment.base.model.flow.nodes.StartNode
import ru.pashkovske.buratino.assignment.base.service.flow.FlowBuilder
import ru.pashkovske.buratino.assignment.base.service.flow.builder.EndNodeBuilder
import ru.pashkovske.buratino.assignment.base.service.flow.builder.ExeNodeBuilder
import ru.pashkovske.buratino.assignment.base.service.flow.builder.RouteNodeBuilder
import java.util.UUID
import kotlin.jvm.java

class FlowBuilderTest {

    @Test
    fun `build basic flow`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId: UUID = flowBuilder.getStart()
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")
        val anotherExeNodeBuilder = ExeNodeBuilder("another_exe", "another_action")
        val endNodeBuilder1 = EndNodeBuilder("end_1")
        val endNodeBuilder2 = EndNodeBuilder("end_2")

        val flow: Flow = flowBuilder
            .addNode(
                node = exeNodeBuilder,
                after = startNodeId
            )
            .addNode(
                node = routeNodeBuilder,
                after = exeNodeBuilder.id
            )
            .addNodeToRoute(
                node = anotherExeNodeBuilder,
                after = routeNodeBuilder.id,
                route = "route_1"
            )
            .addNodeToRoute(
                node = endNodeBuilder2,
                after = routeNodeBuilder.id,
                route = "route_2"
            )
            .addNode(
                node = endNodeBuilder1,
                after = anotherExeNodeBuilder.id
            )
            .build()

        assertEquals(startNodeId, flow.start)

        val startNode: Node? = flow[startNodeId]
        assertNotNull(startNode)
        assertInstanceOf(StartNode::class.java, startNode)
        startNode as StartNode

        val exeNode: Node? = flow[startNode.next]
        assertNotNull(exeNode)
        assertInstanceOf(ExeNode::class.java, exeNode)
        exeNode as ExeNode

        val routeNode: Node? = flow[exeNode.next]
        assertNotNull(routeNode)
        assertInstanceOf(RouteNode::class.java, routeNode)
        routeNode as RouteNode
        assertTrue(routeNode.routes.contains("route_1"))
        assertTrue(routeNode.routes.contains("route_2"))

        val anotherExeNode: Node? = flow[routeNode.routes["route_1"]!!]
        assertNotNull(anotherExeNode)
        assertInstanceOf(ExeNode::class.java, anotherExeNode)
        anotherExeNode as ExeNode

        val endNode: Node? = flow[anotherExeNode.next]
        assertNotNull(endNode)
        assertInstanceOf(EndNode::class.java, endNode)

        val endNode2: Node? = flow[routeNode.routes["route_2"]!!]
        assertNotNull(endNode2)
        assertInstanceOf(EndNode::class.java, endNode2)
    }
}