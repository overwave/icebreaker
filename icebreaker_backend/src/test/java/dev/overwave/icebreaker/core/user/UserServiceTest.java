package dev.overwave.icebreaker.core.user;

import dev.overwave.icebreaker.api.user.UserDto;
import dev.overwave.icebreaker.configuration.FunctionalTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalTest
@RequiredArgsConstructor
class UserServiceTest {
    private final UserService userService;

    @Test
    void loadUserByUsername() {
        userService.registerUser("user", "password");
        UserDetails userDetails = userService.loadUserByUsername("user");
        assertThat(userDetails.getUsername()).isEqualTo("user");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().stream().findFirst().orElseThrow()).isEqualTo(UserRole.CAPTAIN);

        UserDto userDto = userService.selfInfo("user");
        assertThat(userDto.login()).isEqualTo("user");
        assertThat(userDto.roles()).containsExactly(UserRole.CAPTAIN);
    }
}