package ru.pashkovske.buratino.assignment.base.model.action

import ru.pashkovske.buratino.assignment.base.model.Assignment

class AssignmentActionChain<A : Assignment> {
    private val actions: MutableList<AssignmentAction<A>> = mutableListOf()
    private val names: MutableSet<String> = mutableSetOf()

    operator fun set(
        after: String,
        action: AssignmentAction<A>
    ) {
        registerName(action)
        actions.add(
            index = actions.indexOfFirst { act -> act.name == after } + 1,
            element = action
        )
    }

    operator fun plusAssign(action: AssignmentAction<A>) {
        registerName(action)
        names.add(action.name)
        actions.add(action)
    }

    operator fun invoke(assignment: A): A {
        var currentAssignment = assignment
        actions.forEach { action ->
            val result = action(currentAssignment)
            currentAssignment = result.assignment
            if (!result.shouldContinue) {
                return currentAssignment
            }
        }
        return currentAssignment
    }

    private fun registerName(action: AssignmentAction<A>) {
        if (action.name in names) {
            throw IllegalArgumentException("Action with name `${action.name}` already registered.")
        }
        names.add(action.name)
    }

    fun getNames(): List<String> {
        return actions.map(AssignmentAction<A>::name)
    }
}
