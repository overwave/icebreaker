package dev.overwave.icebreaker;

import dev.overwave.icebreaker.configuration.FunctionalTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@FunctionalTest
class IcebreakerApplicationTests {
    private final ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

}