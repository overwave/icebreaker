package dev.overwave.icebreaker.core.user;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public record UserDetailsDto(
        String login,
        String password,
        UserRole role
) implements UserDetails {

    @Override
    public List<UserRole> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }
}