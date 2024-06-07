package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.navigation.NavigationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping(path = "/icebreaker/api/navigation", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NavigationRequestController {
    private final NavigationPointService navigationPointService;
    private final NavigationRequestService navigationRequestService;

    @GetMapping("/reference-points")
    public List<NavigationPointDto> getNavigationPoints() {
        return navigationPointService.getNavigationPoints();
    }

    @PutMapping("/reference-points")
    public void resetNavigationPoints(@RequestBody File file) {
        navigationPointService.resetNavigationPoints(file);
    }

    @PostMapping("/request")
    public void addNavigationRequest(@RequestBody NavigationRequestDto requestDto) {
        navigationRequestService.saveNavigationRequest(requestDto);
    }
}
