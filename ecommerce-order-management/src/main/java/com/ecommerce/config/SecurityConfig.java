package com.ecommerce.config;

import com.ecommerce.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class SecurityConfig
{

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf(csrf -> csrf.disable()).sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register","/api/auth/login","/").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/products/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/inventory/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/inventory/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/orders/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/users/me")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        .anyRequest()
                        .authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception
    {
        return configuration.getAuthenticationManager();
    }


}
