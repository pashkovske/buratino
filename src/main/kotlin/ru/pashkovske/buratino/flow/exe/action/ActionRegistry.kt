package ru.pashkovske.buratino.flow.exe.action

import ru.pashkovske.buratino.flow.exe.context.ExeCtx

class ActionRegistry<Ctx: ExeCtx> {

    private val actions: MutableMap<String, ActionExe<Ctx>> = mutableMapOf()

    operator fun get(name: String): ActionExe<Ctx>? = actions[name]

    operator fun contains(name: String): Boolean = actions.containsKey(name)

    fun registerAction(action: ActionExe<Ctx>) {
        if (actions.containsKey(action.name)) {
            throw IllegalArgumentException("Action ${action.name} already registered")
        }
        actions[action.name] = action
    }
}
