package dev.overwave.icebreaker.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserExistsException extends ResponseStatusException {

    public UserExistsException(String login) {
        super(HttpStatus.CONFLICT, String.format("Login %s already used", login));
    }
}