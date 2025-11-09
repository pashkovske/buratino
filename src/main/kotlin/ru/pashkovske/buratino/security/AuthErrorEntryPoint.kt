package ru.pashkovske.buratino.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthErrorEntryPoint: AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        val errorType: AuthErrorType = request.getAttribute(PredefinedApiKeyAuthFilter.AUTH_ERROR_ATTR) as? AuthErrorType ?: AuthErrorType.UNKNOWN
        when (errorType) {
            AuthErrorType.MISSING_API_KEY -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            AuthErrorType.INVALID_API_KEY -> response.sendError(HttpServletResponse.SC_FORBIDDEN)
            else -> response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown authorization error")
        }
    }
}