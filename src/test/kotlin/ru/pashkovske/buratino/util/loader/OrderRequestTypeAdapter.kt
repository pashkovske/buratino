package ru.pashkovske.buratino.util.loader

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import ru.pashkovske.buratino.order.model.OrderRequest
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import java.lang.reflect.Type

class OrderRequestTypeAdapter : JsonDeserializer<OrderRequest> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OrderRequest {
        val jsonObject = json?.asJsonObject ?: throw IllegalArgumentException("Cannot deserialize null to OrderRequest")
        val typeDescriptor: String = jsonObject.get("_type_descriptor").asString
        jsonObject.remove("_type_descriptor")
        return when (typeDescriptor) {
            "LimitOrderRequest" -> context?.deserialize<LimitOrderRequest>(jsonObject, LimitOrderRequest::class.java)
                ?: throw IllegalArgumentException("Cannot deserialize LimitOrderRequest")
            else -> throw IllegalArgumentException("Unknown OrderRequest type")
        }
    }
}
