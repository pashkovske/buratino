package ru.pashkovske.buratino.flow.exe.action

class ActionRegistry<T> {

    private val actions: MutableMap<String, ActionExe<T>> = mutableMapOf()

    operator fun get(name: String): ActionExe<T>? = actions[name]

    operator fun contains(name: String): Boolean = actions.containsKey(name)

    fun registerAction(action: ActionExe<T>) {
        if (actions.containsKey(action.name)) {
            throw IllegalArgumentException("Action ${action.name} already registered")
        }
        actions[action.name] = action
    }
}
