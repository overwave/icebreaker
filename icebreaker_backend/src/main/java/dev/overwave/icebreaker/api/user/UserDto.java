package dev.overwave.icebreaker.api.user;

import dev.overwave.icebreaker.core.user.UserRole;

public record UserDto(
        String login,
        UserRole role
) {
}