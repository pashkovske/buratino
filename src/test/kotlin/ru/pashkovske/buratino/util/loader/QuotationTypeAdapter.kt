package ru.pashkovske.buratino.util.loader

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import ru.pashkovske.buratino.price.quotation.model.Quotation
import java.lang.reflect.Type

class QuotationTypeAdapter : JsonDeserializer<Quotation> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Quotation {
        val keyStr = json?.asString ?: throw IllegalArgumentException("Cannot deserialize null to Quotation")
        val parts = keyStr.split(".")
        val units = parts[0].toLong()
        val nano = if (parts.size > 1) {
            parts[1].padEnd(9, '0').toInt()
        } else 0
        return Quotation(units, nano)
    }
}
