package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.navigation.NavigationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/icebreaker/api/navigation", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NavigationRequestController {
    private final NavigationPointService navigationPointService;
    private final NavigationRequestService navigationRequestService;

    @GetMapping("/navigation-points")
    public List<NavigationPointDto> getNavigationPoints() {
        return navigationPointService.getNavigationPoints();
    }

    @SneakyThrows
    @PutMapping("/navigation-points")
    public void resetNavigationPoints(@RequestParam MultipartFile file) {
        navigationPointService.resetNavigationPoints(file.getInputStream());
    }

    @PutMapping("/request")
    public void addNavigationRequest(@RequestBody NavigationRequestDto requestDto) {
        navigationRequestService.saveNavigationRequest(requestDto);
    }
}
