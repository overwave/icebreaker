package dev.overwave.icebreaker.core.navigation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipService {
    private final ShipRepository shipRepository;
}
