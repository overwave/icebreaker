package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.ReferencePointDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferencePointService {
    private final ReferencePointRepository referencePointRepository;
    private final ReferencePointMapper mapper;
    
    public List<ReferencePointDto> getReferencePoints() {
        List<ReferencePoint> points = referencePointRepository.findAll();
        return points.stream()
                .map(mapper::toReferencePointDto)
                .collect(Collectors.toList());
    }
}
