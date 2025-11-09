package ru.pashkovske.buratino.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class PredefinedApiKeyAuthFilter(
    @Value("\${auth.predefined.api-key}") apiKey: String
): OncePerRequestFilter() {

    companion object {
        const val AUTH_ERROR_ATTR = "auth.error"
    }

    private val apiKeyHash: Int = apiKey.hashCode()

    init {
        if (apiKey.isEmpty()) {
            throw IllegalArgumentException("API key is empty")
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestApiKey: String? = request.getHeader("X-API-KEY")

        when {
            requestApiKey == null -> {
                request.setAttribute(AUTH_ERROR_ATTR, AuthErrorType.MISSING_API_KEY)
            }
            requestApiKey.hashCode() != apiKeyHash -> {
                logger.warn("Invalid API key: `${maskApiKey(requestApiKey)}`")
                request.setAttribute(AUTH_ERROR_ATTR, AuthErrorType.INVALID_API_KEY)
            }
            else -> {
                val username = "anonymous"
                val authorities: List<GrantedAuthority> = listOf(
                    SimpleGrantedAuthority("ROLE_USER")
                )
                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun maskApiKey(apiKey: String): String {
        val maskedApiKey: CharArray = apiKey.toCharArray()
        for (i in 0 until apiKey.length) {
            maskedApiKey[i] = if (i % 2 == 0) '*' else apiKey[i]
        }
        return maskedApiKey.concatToString()
    }
}
