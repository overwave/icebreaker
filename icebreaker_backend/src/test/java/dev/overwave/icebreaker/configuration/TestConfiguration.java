package dev.overwave.icebreaker.configuration;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

@org.springframework.boot.test.context.TestConfiguration
@MockBean(PasswordEncoder.class)
@ComponentScan("dev.overwave.icebreaker")
public class TestConfiguration {
}
