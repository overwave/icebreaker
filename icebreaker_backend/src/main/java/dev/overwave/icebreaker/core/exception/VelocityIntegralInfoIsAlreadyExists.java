package dev.overwave.icebreaker.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class VelocityIntegralInfoIsAlreadyExists extends ResponseStatusException {

    public VelocityIntegralInfoIsAlreadyExists() {
        super(HttpStatus.CONFLICT, ("Velocity integral info is already used"));
    }
}
