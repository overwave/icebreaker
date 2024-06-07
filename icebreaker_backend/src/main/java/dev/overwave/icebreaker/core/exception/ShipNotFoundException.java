package dev.overwave.icebreaker.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ShipNotFoundException extends ResponseStatusException {

    public ShipNotFoundException(long id) {
        super(HttpStatus.NOT_FOUND, String.format("Ship %d not found", id));
    }

}
