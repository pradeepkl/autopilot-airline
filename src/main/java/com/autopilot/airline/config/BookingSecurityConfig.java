package com.autopilot.airline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Level 4: ownership transfer. Declaring these two beans makes the default
 * security chain and the generated-password user back off.
 */
@Configuration
class BookingSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    // {noop} keeps the demo readable. Use a real PasswordEncoder outside a demo.
    @Bean
    UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("passenger").password("{noop}passenger123").roles("PASSENGER").build(),
                User.withUsername("admin").password("{noop}admin123").roles("ADMIN").build());
    }
}
