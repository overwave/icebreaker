package dev.overwave.icebreaker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@org.springframework.boot.test.context.TestConfiguration
@ComponentScan("dev.overwave.icebreaker")
public class TestConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder mock = mock(PasswordEncoder.class);
        when(mock.encode(any())).thenAnswer(a -> a.getArguments()[0]);
        return mock;
    }
}
