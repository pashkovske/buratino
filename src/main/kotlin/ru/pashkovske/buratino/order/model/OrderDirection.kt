package ru.pashkovske.buratino.order.model

enum class OrderDirection {
    BUY,
    SELL;

    companion object {
        fun fromString(value: String): OrderDirection {
            return valueOf(value.uppercase())
        }
    }

    fun getOpposite(): OrderDirection {
        return when (this) {
            BUY -> SELL
            SELL -> BUY
        }
    }
}