package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.NavigationRequestService;
import dev.overwave.icebreaker.core.navigation.ReferencePointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping(path = "/icebreaker/api/navigation", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NavigationRequestController {
    private final ReferencePointService referencePointService;
    private final NavigationRequestService navigationRequestService;

    @GetMapping("/reference-points")
    public List<ReferencePointDto> getReferencePoints() {
        return referencePointService.getReferencePoints();
    }

    @PostMapping("/reference-points")
    public void getReferencePoints(@RequestBody File file) {
        referencePointService.saveReferencePoints(file);
    }

    @PostMapping("/request")
    public void addNavigationRequest(@RequestBody NavigationRequestDto requestDto) {
        navigationRequestService.saveNavigationRequest(requestDto);
    }
}
