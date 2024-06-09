package dev.overwave.icebreaker.core.user;

import dev.overwave.icebreaker.api.exception.UserExistsException;
import dev.overwave.icebreaker.api.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        return new UserDetailsDto(user.getLogin(), user.getPassword(), user.getRoles());
    }

    public void registerUser(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new UserExistsException(login);
        }
        userRepository.save(User.builder()
                .login(login)
                .password(passwordEncoder.encode(password))
                .name("Anonymous")
                .roles(new UserRole[]{UserRole.CAPTAIN})
                .build());
    }

    public UserDto selfInfo(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        return new UserDto(user.getLogin(), user.getRoles());
    }
}