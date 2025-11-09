package ru.pashkovske.buratino.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import kotlin.jvm.java

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(
        authFilter: PredefinedApiKeyAuthFilter,
        authErrorEntryPoint: AuthErrorEntryPoint,
        http: HttpSecurity
    ): SecurityFilterChain {
        return http.csrf { it.disable() }
            .authorizeHttpRequests { auth: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry ->
                auth
                    .anyRequest().hasRole("USER")
            }
            .exceptionHandling { exceptionHandlingConfigurer: ExceptionHandlingConfigurer<HttpSecurity> ->
                exceptionHandlingConfigurer.authenticationEntryPoint(authErrorEntryPoint)
            }
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}
