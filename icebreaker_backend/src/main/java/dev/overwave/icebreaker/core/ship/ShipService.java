package dev.overwave.icebreaker.core.ship;

import dev.overwave.icebreaker.api.ship.ShipCreateRequest;
import dev.overwave.icebreaker.api.ship.ShipDto;
import dev.overwave.icebreaker.core.user.User;
import dev.overwave.icebreaker.core.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipService {
    private final ShipRepository shipRepository;
    private final UserRepository userRepository;
    private final ShipMapper shipMapper;

    @Transactional
    public List<ShipDto> getShips(String username) {
        User user = userRepository.findByLoginOrThrow(username);
        return user.getShips().stream().map(shipMapper::map).toList();
    }

    public ShipDto createShip(ShipCreateRequest ship, String username) {
        User user = userRepository.findByLoginOrThrow(username);
        return shipMapper.map(shipRepository.save(new Ship(ship.name(), ship.iceClass(), ship.speed(), false, user)));
    }
}
