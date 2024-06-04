package dev.overwave.icebreaker.core.user;

import dev.overwave.icebreaker.api.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    default User findByLoginOrThrow(String login) {
        return findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
    }
}