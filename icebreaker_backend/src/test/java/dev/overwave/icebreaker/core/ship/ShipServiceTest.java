package dev.overwave.icebreaker.core.ship;

import dev.overwave.icebreaker.api.ship.ShipCreateRequest;
import dev.overwave.icebreaker.api.ship.ShipDto;
import dev.overwave.icebreaker.api.user.RegisterUserRequestDto;
import dev.overwave.icebreaker.configuration.FunctionalTest;
import dev.overwave.icebreaker.core.user.UserRole;
import dev.overwave.icebreaker.core.user.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalTest
@RequiredArgsConstructor
class ShipServiceTest {
    private final ShipService shipService;
    private final UserService userService;

    @Test
    void getShips() {
        userService.registerUser(new RegisterUserRequestDto("user", "password", UserRole.CAPTAIN));
        ShipDto ship = shipService.createShip(new ShipCreateRequest("Плот", 19, IceClass.ARC_4), "user");
        assertThat(ship).isEqualTo(new ShipDto(ship.id(), "Плот", 19, IceClass.ARC_4.name()));

        List<ShipDto> ships = shipService.getShips("user");
        assertThat(ships).containsExactly(new ShipDto(ship.id(), "Плот", 19, IceClass.ARC_4.name()));
    }
}