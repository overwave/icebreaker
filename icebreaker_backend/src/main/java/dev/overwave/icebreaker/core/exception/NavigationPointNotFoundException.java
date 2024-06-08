package dev.overwave.icebreaker.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NavigationPointNotFoundException extends ResponseStatusException {

    public NavigationPointNotFoundException(long id) {
        super(HttpStatus.NOT_FOUND, String.format("Navigation point %d not found", id));
    }
}
