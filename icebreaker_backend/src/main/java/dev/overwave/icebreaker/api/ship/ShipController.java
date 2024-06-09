package dev.overwave.icebreaker.api.ship;

import dev.overwave.icebreaker.core.ship.IceClass;
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
import java.util.List;

@RestController
@RequestMapping(path = "/icebreaker/api/ship", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ShipController {
    private final ShipService shipService;

    @GetMapping("/ships")
    public List<ShipDto> getShips(Principal principal) {
        return shipService.getShips(principal.getName());
    }

    @SneakyThrows
    @PutMapping("/ships")
    public ShipDto createShip(@RequestBody ShipCreateRequest ship, Principal principal) {
        return shipService.createShip(ship, principal.getName());
    }

    @GetMapping("/ice-classes")
    public List<IceClass> getIceClasses() {
        return List.of(IceClass.values());
    }

}
