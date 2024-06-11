package dev.overwave.icebreaker.api.ship;

import dev.overwave.icebreaker.core.ship.ShipService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "/icebreaker/api/ship", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ShipController {
    private final ShipService shipService;

    @GetMapping("/ships")
    public ShipListDto getShips(Principal principal) {
        return new ShipListDto(shipService.getShips(principal.getName()));
    }

    @SneakyThrows
    @PutMapping("/ships")
    public ShipDto createShip(@RequestBody ShipCreateRequest ship, Principal principal) {
        return shipService.createShip(ship, principal.getName());
    }

    @GetMapping("/ice-classes")
    public IceClassListDto getIceClasses() {
        return new IceClassListDto(shipService.getIceClasses());
    }

}
