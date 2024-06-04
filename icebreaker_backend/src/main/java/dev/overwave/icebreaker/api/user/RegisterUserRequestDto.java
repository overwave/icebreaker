package dev.overwave.icebreaker.api.user;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDto(
        @NotBlank
        String login,
        @NotBlank
        String password
) {
}