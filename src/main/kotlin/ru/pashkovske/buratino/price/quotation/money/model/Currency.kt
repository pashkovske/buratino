package ru.pashkovske.buratino.price.quotation.money.model

enum class Currency {
    RUB,
    USD,
    EUR,
    UNKNOWN;

    companion object {
        fun fromStr(input: String): Currency {
            return Currency
                .entries
                .firstOrNull { it.name.equals(input, true) }
                ?: UNKNOWN
        }
    }
}