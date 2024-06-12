package dev.overwave.icebreaker.api.user;

import dev.overwave.icebreaker.core.user.UserRole;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDto(
        @NotBlank
        String login,
        @NotBlank
        String password,
        @NotBlank
        UserRole role
) {
}