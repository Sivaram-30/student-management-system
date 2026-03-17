package com.sms.studentmanagement.config;

import com.sms.studentmanagement.security.AuthTokenFilter;
import com.sms.studentmanagement.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication
    .AuthenticationManager;
import org.springframework.security.authentication.dao
    .DaoAuthenticationProvider;
import org.springframework.security.config.annotation
    .authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation
    .method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation
    .web.builders.HttpSecurity;
import org.springframework.security.config.annotation
    .web.configuration.EnableWebSecurity;
import org.springframework.security.config.http
    .SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt
    .BCryptPasswordEncoder;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication
    .UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors
    .UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider =
        new DaoAuthenticationProvider(passwordEncoder());
    provider.setUserDetailsService(userDetailsService);
    return provider;
}

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration =
            new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
            List.of("*"));
        configuration.setAllowedMethods(Arrays.asList(
            "GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization","Content-Type","X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
            .cors(cors -> cors.configurationSource(
                corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**")
                    .permitAll()
                .requestMatchers("/api/students/**")
                    .hasAnyRole("ADMIN","TEACHER")
                .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                .anyRequest()
                    .authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(
            authenticationJwtTokenFilter(),
            UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

