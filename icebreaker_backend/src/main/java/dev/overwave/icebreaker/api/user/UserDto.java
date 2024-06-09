package dev.overwave.icebreaker.api.user;

import dev.overwave.icebreaker.core.user.UserRole;

import java.util.List;

public record UserDto(
        String login,
        List<UserRole> roles
) {
}