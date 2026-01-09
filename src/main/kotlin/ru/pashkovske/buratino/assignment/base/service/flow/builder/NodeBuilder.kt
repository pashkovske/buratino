package ru.pashkovske.buratino.assignment.base.service.flow.builder

import ru.pashkovske.buratino.assignment.base.model.flow.nodes.Node
import ru.pashkovske.buratino.assignment.base.service.flow.exception.BuildNodeException
import ru.pashkovske.buratino.assignment.base.service.flow.readiness.BuilderReadiness
import java.util.UUID

abstract class NodeBuilder(
    val name: String
) {
    val id: UUID = UUID.randomUUID()
    protected var next: UUID? = null
    protected var previous: UUID? = null

    open fun setNext(next: UUID): NodeBuilder {
        if (this.next != null) {
            throw BuildNodeException(
                message = "Next node is already set to ${this.next}",
                type = this::class,
                name = name,
                id = id
            )
        }
        this.next = next
        return this
    }
    fun getNext(): UUID? {
        return next
    }

    open fun setPrevious(previous: UUID): NodeBuilder {
        if (this.previous != null) {
            throw BuildNodeException(
                message = "Previous node is already set to ${this.next}",
                type = this::class,
                name = name,
                id = id
            )
        }
        this.previous = previous
        return this
    }
    fun getPrevious(): UUID? {
        return previous
    }

    abstract fun build(): Node
    abstract fun validateUndefinedEdges(): BuilderReadiness
}
