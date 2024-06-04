package dev.overwave.icebreaker.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {

    public UserNotFoundException(String login) {
        super(HttpStatus.NOT_FOUND, String.format("User %s not found", login));
    }
}