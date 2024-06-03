package dev.overwave.icebreaker.core.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8081", "https://overwave.dev"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(matcherRegistry -> {
                    matcherRegistry.anyRequest().permitAll();
                })
                .formLogin(loginConfigurer -> {
                    loginConfigurer.loginPage("/icebreaker/login");
                    loginConfigurer.loginProcessingUrl("/icebreaker/api/user/login");
                    loginConfigurer.permitAll();
                })
                .cors(i -> {
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer.logoutUrl("/icebreaker/api/user/logout");
                    logoutConfigurer.deleteCookies("JSESSIONID");
                    logoutConfigurer.permitAll();
                })
                .csrf(AbstractHttpConfigurer::disable) // TODO enable
                .rememberMe(rememberMeConfigurer -> {
                })
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
