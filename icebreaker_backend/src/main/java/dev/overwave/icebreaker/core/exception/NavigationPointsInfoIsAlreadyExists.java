package dev.overwave.icebreaker.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NavigationPointsInfoIsAlreadyExists extends ResponseStatusException {

    public NavigationPointsInfoIsAlreadyExists() {
        super(HttpStatus.CONFLICT, ("Navigation points info is already used"));
    }
}
