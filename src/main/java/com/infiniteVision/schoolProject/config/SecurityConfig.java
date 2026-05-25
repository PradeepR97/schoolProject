package com.infiniteVision.schoolProject.config;

import com.infiniteVision.schoolProject.constants.ApiConstants;
import com.infiniteVision.schoolProject.modules.auth.constants.AuthApiConstants;
import com.infiniteVision.schoolProject.modules.health.constants.HealthApiConstants;
import com.infiniteVision.schoolProject.security.JwtAuthenticationFilter;
import com.infiniteVision.schoolProject.security.SecurityHandlers;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, OtpProperties.class})
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_AUTH_PATHS = {
        ApiConstants.API_V1_PREFIX + "/auth/login",
        AuthApiConstants.FORGOT_PASSWORD,
        AuthApiConstants.VERIFY_OTP,
        AuthApiConstants.RESET_PASSWORD,
        AuthApiConstants.LOGIN_SEND_OTP,
        AuthApiConstants.LOGIN_VERIFY_OTP,
        AuthApiConstants.LOGIN_RESEND_OTP
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityHandlers securityHandlers;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(securityHandlers)
                        .accessDeniedHandler(securityHandlers))
                .authorizeHttpRequests(auth -> auth.requestMatchers(HealthApiConstants.BASE_PATH)
                        .permitAll()
                        .requestMatchers(PUBLIC_AUTH_PATHS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
