package dev.overwave.icebreaker.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public record UserDetailsDto(
        String login,
        String password,
        List<UserRole> roles
) implements UserDetails {

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return roles;
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