package ru.pashkovske.buratino.unit.assignment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.pashkovske.buratino.flow.model.Flow
import ru.pashkovske.buratino.flow.model.nodes.EndNode
import ru.pashkovske.buratino.flow.model.nodes.ExeNode
import ru.pashkovske.buratino.flow.model.nodes.Node
import ru.pashkovske.buratino.flow.model.nodes.RouteNode
import ru.pashkovske.buratino.flow.model.nodes.StartNode
import ru.pashkovske.buratino.flow.builder.FlowBuilder
import ru.pashkovske.buratino.flow.builder.EndNodeBuilder
import ru.pashkovske.buratino.flow.builder.ExeNodeBuilder
import ru.pashkovske.buratino.flow.builder.RouteNodeBuilder
import ru.pashkovske.buratino.flow.exception.BuildNodeException
import ru.pashkovske.buratino.flow.exception.FlowIsNotReadyException
import java.util.UUID
import kotlin.jvm.java

class FlowBuilderTest {

    @Test
    fun `build simplest flow`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId: UUID = flowBuilder.getStart()
        val endNodeBuilder = EndNodeBuilder("end", "test")

        flowBuilder.registerResolution("test")
        val flow: Flow = flowBuilder
            .addNode(
                node = endNodeBuilder,
                after = startNodeId
            )
            .build()

        assertNotNull(flow)

        val startNode: Node? = flow[startNodeId]
        assertNotNull(startNode)
        assertInstanceOf(StartNode::class.java, startNode)
        startNode as StartNode

        val endNode: Node? = flow[startNode.next]
        assertNotNull(endNode)
        assertInstanceOf(EndNode::class.java, endNode)
    }

    @Test
    fun `build with linking before adding`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId: UUID = flowBuilder.getStart()
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val endNodeBuilder = EndNodeBuilder("end", "test")

        flowBuilder.registerResolution("test")
        flowBuilder.registerAction("action")
        exeNodeBuilder.setNext(endNodeBuilder.id)
        exeNodeBuilder.setPrevious(startNodeId)

        val flow: Flow = flowBuilder
            .addNode(endNodeBuilder)
            .addNode(exeNodeBuilder)
            .build()
        assertNotNull(flow)
    }

    @Test
    fun `build with linking but wrong order adding error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId: UUID = flowBuilder.getStart()
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val endNodeBuilder = EndNodeBuilder("end", "test")

        flowBuilder.registerResolution("test")
        flowBuilder.registerAction("action")
        exeNodeBuilder.setNext(endNodeBuilder.id)
        exeNodeBuilder.setPrevious(startNodeId)

        assertThrows { flowBuilder.addNode(exeNodeBuilder) } as BuildNodeException
    }

    @Test
    fun `build with before statement`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId: UUID = flowBuilder.getStart()
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val endNodeBuilder = EndNodeBuilder("end", "test")

        flowBuilder.registerResolution("test")
        flowBuilder.registerAction("action")
        exeNodeBuilder.setPrevious(startNodeId)

        val flow: Flow = flowBuilder
            .addNode(endNodeBuilder)
            .addNode(
                node = exeNodeBuilder,
                before = endNodeBuilder.id
            )
            .build()
        assertNotNull(flow)
    }

    @Test
    fun `build basic flow`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId: UUID = flowBuilder.getStart()
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")
        val anotherExeNodeBuilder = ExeNodeBuilder("another_exe", "another_action")
        val endNodeBuilder1 = EndNodeBuilder("end_1", "test")
        val endNodeBuilder2 = EndNodeBuilder("end_2", "test")

        flowBuilder.registerResolution("test")
        flowBuilder.registerAction("action")
        flowBuilder.registerAction("another_action")
        flowBuilder.registerRouter("router", setOf("route_1", "route_2"))
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

    @Test
    fun `build flow with single route router`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1"), "router")
        val endNodeBuilder = EndNodeBuilder("end", "test")

        flowBuilder.registerResolution("test")
        flowBuilder.registerRouter("router", setOf("route_1", "route_2"))
        val flow = flowBuilder
            .addNode(
                node = routeNodeBuilder,
                after = startNodeId
            )
            .addNodeToRoute(
                node = endNodeBuilder,
                after = routeNodeBuilder.id,
                route = "route_1"
            )
            .build()

        assertNotNull(flow)
        assertEquals(startNodeId, flow.start)

        val startNode: Node? = flow[startNodeId]
        assertNotNull(startNode)
        assertInstanceOf(StartNode::class.java, startNode)
        startNode as StartNode

        val routeNode: Node? = flow[startNode.next]
        assertNotNull(routeNode)
        assertInstanceOf(RouteNode::class.java, routeNode)
        routeNode as RouteNode

        val endNode: Node? = flow[routeNode.routes["route_1"]!!]
        assertNotNull(endNode)
        assertInstanceOf(EndNode::class.java, endNode)
    }

    @Test
    fun `build flow start not complete error`() {
        val flowBuilder = FlowBuilder("test")
        assertThrows { flowBuilder.build() } as FlowIsNotReadyException
    }

    @Test
    fun `build flow start not complete with other nodes error`() {
        val flowBuilder = FlowBuilder("test")
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val endNodeBuilder = EndNodeBuilder("end", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerAction("action")
        flowBuilder
            .addNode(exeNodeBuilder)
            .addNode(
                after = exeNodeBuilder.id,
                node = endNodeBuilder
            )
        assertThrows { flowBuilder.build() } as FlowIsNotReadyException
    }

    @Test
    fun `build flow exe node not complete error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val endNodeBuilder = EndNodeBuilder("end", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerAction("action")
        flowBuilder
            .addNode(
                node = exeNodeBuilder,
                after = startNodeId
            )
            .addNode(endNodeBuilder)
        assertThrows { flowBuilder.build() } as FlowIsNotReadyException
    }

    @Test
    fun `build flow end node not complete error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")
        val endNodeBuilder1 = EndNodeBuilder("end1", "test")
        val endNodeBuilder2 = EndNodeBuilder("end2", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerAction("action")
        flowBuilder
            .addNode(
                node = exeNodeBuilder,
                after = startNodeId
            )
            .addNode(
                node = endNodeBuilder1,
                after = exeNodeBuilder.id
            )
            .addNode(endNodeBuilder2)
        assertThrows { flowBuilder.build() } as FlowIsNotReadyException
    }

    @Test
    fun `buidl flow root node not complete error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")
        val endNodeBuilder = EndNodeBuilder("end", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerRouter("router", setOf("route_1", "route_2"))
        flowBuilder
            .addNode(
                node = endNodeBuilder,
                after = startNodeId
            )
            .addNode(routeNodeBuilder)
        assertThrows { flowBuilder.build() } as FlowIsNotReadyException
    }

    @Test
    fun `build flow route node not complete 1route error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")
        val endNodeBuilder = EndNodeBuilder("end", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerRouter("router", setOf("route_1", "route_2"))
        flowBuilder
            .addNode(
                node = routeNodeBuilder,
                after = startNodeId
            )
            .addNodeToRoute(
                node = endNodeBuilder,
                after = routeNodeBuilder.id,
                route = "route_1"
            )
        assertThrows { flowBuilder.build() } as FlowIsNotReadyException
    }

    @Test
    fun `build flow route node not complete previous error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")
        val endNodeBuilder1 = EndNodeBuilder("end1", "test")
        val endNodeBuilder2 = EndNodeBuilder("end2", "test")
        val endNodeBuilder3 = EndNodeBuilder("end3", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerRouter("router", setOf("route_1", "route_2"))
        flowBuilder
            .addNode(
                node = endNodeBuilder3,
                after = startNodeId
            )
            .addNode(routeNodeBuilder)
            .addNodeToRoute(
                node = endNodeBuilder1,
                after = routeNodeBuilder.id,
                route = "route_1"
            )
            .addNodeToRoute(
                node = endNodeBuilder2,
                after = routeNodeBuilder.id,
                route = "route_2"
            )
        assertThrows { flowBuilder.build() } as FlowIsNotReadyException
    }

    @Test
    fun `try to build flow with same id error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val endNodeBuilder = EndNodeBuilder("end", "test")
        flowBuilder.registerResolution("test")
        flowBuilder
            .addNode(
                node = endNodeBuilder,
                after = startNodeId
            )
        assertThrows { flowBuilder.addNode(endNodeBuilder) } as BuildNodeException
    }

    @Test
    fun `try to add node to same start link error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val endNodeBuilder = EndNodeBuilder("end", "test")
        val endNodeBuilder2 = EndNodeBuilder("end2", "test")
        flowBuilder.registerResolution("test")
        flowBuilder
            .addNode(
                node = endNodeBuilder,
                after = startNodeId
            )
        assertThrows {
            flowBuilder.addNode(
                node = endNodeBuilder2,
                after = startNodeId
            )
        } as BuildNodeException
    }

    @Test
    fun `try to add same node to different start link error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val endNodeBuilder = EndNodeBuilder("end", "test")
        val endNodeBuilder2 = EndNodeBuilder("end2", "test")
        flowBuilder.registerResolution("test")
        flowBuilder
            .addNode(
                node = endNodeBuilder,
                after = startNodeId
            )
        assertThrows {
            flowBuilder.addNode(
                node = endNodeBuilder2,
                after = endNodeBuilder.id
            )
        } as BuildNodeException
    }

    @Test
    fun `try to add node to same route error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")
        val endNodeBuilder = EndNodeBuilder("end", "test")
        val endNodeBuilder2 = EndNodeBuilder("end2", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerRouter("router", setOf("route_1", "route_2"))
        flowBuilder
            .addNode(
                node = routeNodeBuilder,
                after = startNodeId
            )
            .addNodeToRoute(
                node = endNodeBuilder,
                after = routeNodeBuilder.id,
                route = "route_1"
            )
        assertThrows {
            flowBuilder.addNodeToRoute(
                node = endNodeBuilder2,
                after = routeNodeBuilder.id,
                route = "route_1"
            )
        } as BuildNodeException
    }

    @Test
    fun `try to add node to not existing route error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")
        val endNodeBuilder = EndNodeBuilder("end", "test")
        flowBuilder.registerResolution("test")
        flowBuilder.registerRouter("router", setOf("route_1", "route_2"))
        flowBuilder
            .addNode(
                node = routeNodeBuilder,
                after = startNodeId
            )
        assertThrows {
            flowBuilder.addNodeToRoute(
                node = endNodeBuilder,
                after = routeNodeBuilder.id,
                route = "route_3"
            )
        } as BuildNodeException
    }

    @Test
    fun `try to add node before start node error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val endNodeBuilder = EndNodeBuilder("end", "test")
        flowBuilder.registerResolution("test")
        assertThrows {
            flowBuilder.addNode(
                node = endNodeBuilder,
                before = startNodeId
            )
        } as BuildNodeException
    }

    @Test
    fun `try to add node after end node error`() {
        val flowBuilder = FlowBuilder("test")
        val startNodeId = flowBuilder.getStart()
        val endNodeBuilder = EndNodeBuilder("end", "test")
        val endNodeBuilder2 = EndNodeBuilder("end2", "test")
        flowBuilder.registerResolution("test")
        flowBuilder
            .addNode(
                node = endNodeBuilder,
                after = startNodeId
            )
        assertThrows {
            flowBuilder.addNode(
                node = endNodeBuilder2,
                after = endNodeBuilder.id
            )
        } as BuildNodeException
    }

    @Test
    fun `try to add not registered resolution error`() {
        val flowBuilder = FlowBuilder("test")
        val endNodeBuilder = EndNodeBuilder("end", "test")

        assertThrows { flowBuilder.addNode(endNodeBuilder) } as BuildNodeException
    }

    @Test
    fun `try to add not registered action error`() {
        val flowBuilder = FlowBuilder("test")
        val exeNodeBuilder = ExeNodeBuilder("exe", "action")

        assertThrows { flowBuilder.addNode(exeNodeBuilder) } as BuildNodeException
    }

    @Test
    fun `try to add not registered router error`() {
        val flowBuilder = FlowBuilder("test")
        val routeNodeBuilder = RouteNodeBuilder("route", setOf("route_1", "route_2"), "router")

        assertThrows { flowBuilder.addNode(routeNodeBuilder) } as BuildNodeException
    }
}