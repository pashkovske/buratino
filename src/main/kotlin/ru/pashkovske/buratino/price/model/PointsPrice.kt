package ru.pashkovske.buratino.price.model

data class PointsPrice(
    val unit: Long,
    val nano: Int
) {
    override fun toString(): String {
        return "$unit.$nano"
            .trimEnd('0')
            .trimEnd('.')
    }
}
