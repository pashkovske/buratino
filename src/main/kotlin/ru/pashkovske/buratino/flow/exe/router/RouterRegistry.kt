package ru.pashkovske.buratino.flow.exe.router

import ru.pashkovske.buratino.flow.exe.context.ExeCtx

class RouterRegistry<Ctx : ExeCtx> {

    private val routers: MutableMap<String, RouterExe<Ctx>> = mutableMapOf()

    operator fun get(name: String): RouterExe<Ctx>? = routers[name]

    operator fun contains(name: String): Boolean = routers.containsKey(name)

    fun registerRouter(router: RouterExe<Ctx>) {
        if (routers.containsKey(router.name)) {
            throw IllegalArgumentException("Router with name ${router.name} already exists")
        }
        routers[router.name] = router
    }
}