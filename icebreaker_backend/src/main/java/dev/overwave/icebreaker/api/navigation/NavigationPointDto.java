package dev.overwave.icebreaker.api.navigation;

public record NavigationPointDto(
        long id,
        String name,
        float lat,
        float lon
) {
}
