package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.geospatial.VelocityIntervalService;
import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.navigation.NavigationRequestService;
import dev.overwave.icebreaker.core.navigation.RequestStatus;
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

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/icebreaker/api/navigation", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NavigationRequestController {
    private final NavigationPointService navigationPointService;
    private final NavigationRequestService navigationRequestService;
    private final VelocityIntervalService velocityIntervalService;

    @GetMapping("/navigation-points")
    public List<NavigationPointDto> getNavigationPoints() {
        return navigationPointService.getNavigationPoints();
    }

    @SneakyThrows
    @PutMapping("/navigation-points")
    public void resetNavigationPoints(@RequestParam MultipartFile file) {
        navigationPointService.resetNavigationPoints(file.getInputStream());
    }

    @PutMapping("/route-requests")
    public void addNavigationRequest(@RequestBody NavigationRequestDto requestDto) {
        navigationRequestService.saveNavigationRequest(requestDto);
    }

    @GetMapping("/route-requests")
    public Map<RequestStatus, List<NavigationRequestDto>> getNavigationRequests(UserPrincipal userPrincipal) {
        return navigationRequestService.getNavigationRequests(userPrincipal.getName());
    }

    @GetMapping("/velocity-intervals")
    public List<VelocityIntervalDto> getVelocityIntervals() {
        return velocityIntervalService.getVelocityIntervals();
    }

    @SneakyThrows
    @PutMapping("/integral-velocities")
    public void resetIntegralVelocities(@RequestParam MultipartFile file) {
        velocityIntervalService.resetIntegralVelocities(file.getInputStream());
    }

}
