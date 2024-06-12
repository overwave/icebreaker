package dev.overwave.icebreaker.core.user;

import dev.overwave.icebreaker.api.exception.UserExistsException;
import dev.overwave.icebreaker.api.user.RegisterUserRequestDto;
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
        return new UserDetailsDto(user.getLogin(), user.getPassword(), user.getRole());
    }

    public void registerUser(RegisterUserRequestDto requestDto) {
        if (userRepository.findByLogin(requestDto.login()).isPresent()) {
            throw new UserExistsException(requestDto.login());
        }
        userRepository.save(User.builder()
                .login(requestDto.login())
                .password(passwordEncoder.encode(requestDto.password()))
                .name("Anonymous")
                .role(requestDto.role())
                .build());
    }

    public UserDto selfInfo(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        return new UserDto(user.getLogin(), user.getRole());
    }
}