package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.ReferencePointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/icebreaker/api/navigation", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NavigationRequestController {
    private final ReferencePointService referencePointService;

    @GetMapping("/reference-points")
    public List<ReferencePointDto> getReferencePoints() {
        return referencePointService.getReferencePoints();
    }
}
