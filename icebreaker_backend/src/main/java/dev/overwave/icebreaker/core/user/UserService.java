package dev.overwave.icebreaker.core.user;

import dev.overwave.icebreaker.api.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) {
        throw new UnsupportedOperationException("TODO");
    }

    public void registerUser(String login, String password) {
        throw new UnsupportedOperationException("TODO");
    }

    public UserDto selfInfo(String login) {
        return new UserDto("lizunya");
    }
}