package ru.pashkovske.buratino.order.model

enum class OrderDirection {
    BUY,
    SELL;

    companion object {
        fun fromString(value: String): OrderDirection {
            return valueOf(value.uppercase())
        }
    }
}