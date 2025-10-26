package ru.pashkovske.buratino.util.loader

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import ru.pashkovske.buratino.order.model.OrderRequest
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
                    QuotationTypeAdapter()
                )
                .registerTypeAdapter(
                    OrderRequest::class.java,
                    OrderRequestTypeAdapter()
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
