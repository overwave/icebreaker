package dev.overwave.icebreaker.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.api.user.LoginDto;
import dev.overwave.icebreaker.api.user.LoginStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8090",
                "https://overwave.dev"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(matcherRegistry ->
                        matcherRegistry.requestMatchers(
                                        "/icebreaker/api/user/me",
                                        "/icebreaker/api/ship/ships",
                                        "/icebreaker/api/schedule/schedules"
                                ).authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin(loginConfigurer -> {
                    loginConfigurer.loginProcessingUrl("/icebreaker/api/user/login");
                    loginConfigurer.successHandler((a, response, b) -> respondStatus(response, LoginStatus.SUCCESS));
                    loginConfigurer.failureHandler((a, response, b) -> {
                        respondStatus(response, LoginStatus.FAILED);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    });
                    loginConfigurer.permitAll();
                })
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(
                        (request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutUrl("/icebreaker/api/user/logout")
                                .logoutSuccessHandler((a, response, b) -> respondStatus(response, LoginStatus.SUCCESS))
                                .deleteCookies("JSESSIONID")
                                .permitAll())
                .csrf(AbstractHttpConfigurer::disable) // TODO enable
                .rememberMe(rememberMeConfigurer -> {
                    rememberMeConfigurer.rememberMeCookieName("logged_id");
                    rememberMeConfigurer.tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30));
                    rememberMeConfigurer.useSecureCookie(true);
                    rememberMeConfigurer.key("secret");
                }).build();
    }

    private void respondStatus(HttpServletResponse response, LoginStatus success) throws IOException {
        response.getWriter().write(objectMapper.writeValueAsString(new LoginDto(success)));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
