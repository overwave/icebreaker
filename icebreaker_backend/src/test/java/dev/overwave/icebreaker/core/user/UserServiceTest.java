package dev.overwave.icebreaker.core.user;

import dev.overwave.icebreaker.api.user.UserDto;
import dev.overwave.icebreaker.configuration.FunctionalTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@FunctionalTest
@RequiredArgsConstructor
class UserServiceTest {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        when(passwordEncoder.encode(any())).thenAnswer(a -> a.getArguments()[0]);
    }

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