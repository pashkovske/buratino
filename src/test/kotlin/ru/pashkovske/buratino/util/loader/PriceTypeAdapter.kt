package ru.pashkovske.buratino.util.loader

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import java.lang.reflect.Type

class PriceTypeAdapter : JsonDeserializer<Price> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Price {
        if (json == null) {
            throw IllegalArgumentException("Cannot deserialize null to Price")
        }
        
        // Handle JsonObject format (with units, nano, currency fields)
        if (json.isJsonObject) {
            val jsonObject = json.asJsonObject
            val units = jsonObject.get("units").asLong
            val nano = jsonObject.get("nano").asInt
            val currencyStr = jsonObject.get("currency").asString
            val currency = Currency.fromStr(currencyStr)
            return Price(units, nano, currency)
        }
        
        // Handle string format (like "65.5 RUB")
        if (json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            val keyStr = json.asString
            return Price.fromString(keyStr)
        }
        
        throw IllegalArgumentException("Unsupported JSON format for Price: $json")
    }
}
