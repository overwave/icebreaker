package dev.overwave.icebreaker.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NavigationPointsInfoAlreadyExists extends ResponseStatusException {

    public NavigationPointsInfoAlreadyExists() {
        super(HttpStatus.CONFLICT, ("Navigation points info is already used"));
    }
}
