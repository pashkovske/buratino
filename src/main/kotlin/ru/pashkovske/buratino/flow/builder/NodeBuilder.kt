package ru.pashkovske.buratino.flow.builder

import ru.pashkovske.buratino.flow.model.nodes.Node
import ru.pashkovske.buratino.flow.exception.BuildNodeException
import ru.pashkovske.buratino.flow.readiness.BuilderReadiness
import java.util.UUID

abstract class NodeBuilder(
    val name: String
) {
    val id: UUID = UUID.randomUUID()
    protected var nextNode: UUID? = null
    protected var previousNode: UUID? = null

    open fun setNext(next: UUID): NodeBuilder {
        if (this.nextNode != null) {
            throw BuildNodeException(
                message = "Next node is already set to ${this.nextNode}",
                type = this::class,
                name = name,
                id = id
            )
        }
        this.nextNode = next
        return this
    }
    fun getNext(): UUID? {
        return nextNode
    }

    open fun setPrevious(previous: UUID): NodeBuilder {
        if (this.previousNode != null) {
            throw BuildNodeException(
                message = "Previous node is already set to ${this.nextNode}",
                type = this::class,
                name = name,
                id = id
            )
        }
        this.previousNode = previous
        return this
    }
    fun getPrevious(): UUID? {
        return previousNode
    }

    abstract fun build(): Node
    abstract fun validateUndefinedEdges(): BuilderReadiness
}
