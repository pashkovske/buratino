package ru.pashkovske.buratino.instrument.model

data class InstrumentId(
    val id: String
) {
    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstrumentId

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
