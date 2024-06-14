package dev.overwave.icebreaker.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NavigationRequestNotFoundException extends ResponseStatusException {

    public NavigationRequestNotFoundException(long id) {
        super(HttpStatus.NOT_FOUND, String.format("Navigation request %d not found", id));
    }
}
