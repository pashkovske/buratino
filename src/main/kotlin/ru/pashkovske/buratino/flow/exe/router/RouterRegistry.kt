package ru.pashkovske.buratino.flow.exe.router

class RouterRegistry<T> {

    private val routers: MutableMap<String, RouterExe<T>> = mutableMapOf()

    operator fun get(name: String): RouterExe<T>? = routers[name]

    operator fun contains(name: String): Boolean = routers.containsKey(name)

    fun registerRouter(router: RouterExe<T>) {
        if (routers.containsKey(router.name)) {
            throw IllegalArgumentException("Router with name ${router.name} already exists")
        }
        routers[router.name] = router
    }
}