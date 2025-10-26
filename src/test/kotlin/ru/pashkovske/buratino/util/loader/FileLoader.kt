package ru.pashkovske.buratino.util.loader

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSyntaxException
import ru.pashkovske.buratino.order.model.OrderRequest
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.quotation.model.Quotation
import java.io.FileNotFoundException
import java.time.Instant

object FileLoader {
    fun <T> loadFromJson(
        path: String,
        clazz: Class<T>
    ): T {
        try {
            val resourcePath = if (path.startsWith("/")) path.substring(1) else path
            val resource = this::class.java.classLoader.getResource(resourcePath)
                ?: throw FileNotFoundException("Resource not found: $resourcePath")
            val content = resource.readText()
            val processedContent: String = replacePlaceholders(content)
            val gson = GsonBuilder()
                .registerTypeAdapter(
                    Instant::class.java,
                    InstantTypeAdapter()
                )
                .registerTypeAdapter(
                    Quotation::class.java,
                    JsonDeserializer { json, _, _ ->
                        val keyStr = json.asString
                        val parts = keyStr.split(".")
                        val units = parts[0].toLong()
                        val nano = if (parts.size > 1) {
                            // Pad with zeros to 9 digits
                            parts[1].padEnd(9, '0').toInt()
                        } else 0
                        Quotation(units, nano)
                    }
                )
                .registerTypeAdapter(
                    OrderRequest::class.java,
                    JsonDeserializer { json, _, context ->
                        val jsonObject = json.asJsonObject
                        val typeDescriptor: String = jsonObject.get("_type_descriptor").asString
                        jsonObject.remove("_type_descriptor")
                        when (typeDescriptor) {
                            "LimitOrderRequest" -> context.deserialize<LimitOrderRequest>(jsonObject, LimitOrderRequest::class.java)
                            else -> throw IllegalArgumentException("Unknown OrderRequest type")
                        }
                    }
                )
                .create()
            return gson.fromJson(processedContent, clazz)
        } catch (e: FileNotFoundException) {
            throw RuntimeException("File not found: $path", e)
        } catch (e: JsonSyntaxException) {
            throw RuntimeException("Invalid JSON syntax in file: $path", e)
        } catch (e: Exception) {
            throw RuntimeException("Error loading JSON from file: $path", e)
        }
    }

    private fun replacePlaceholders(json: String): String {
        return json.replace(
            oldValue = "\${now}",
            newValue = Instant.now().toString()
        )
    }
}