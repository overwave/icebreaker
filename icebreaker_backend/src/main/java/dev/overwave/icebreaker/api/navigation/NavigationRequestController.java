package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.geospatial.VelocityIntervalService;
import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.navigation.NavigationRequestService;
import dev.overwave.icebreaker.core.route.DefaultRouteService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping(path = "/icebreaker/api/navigation", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NavigationRequestController {
    private final NavigationPointService navigationPointService;
    private final NavigationRequestService navigationRequestService;
    private final VelocityIntervalService velocityIntervalService;
    private final DefaultRouteService defaultRouteService;

    @GetMapping("/navigation-points")
    public NavigationPointListDto getNavigationPoints() {
        return new NavigationPointListDto(navigationPointService.getNavigationPoints());
    }

    @SneakyThrows
    @PutMapping("/navigation-points")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void resetNavigationPoints(@RequestParam MultipartFile file) {
        navigationPointService.resetNavigationPoints(file.getInputStream());
    }

    @PutMapping("/route-requests")
    //TODO поправить дату подачи - LocalDate
    public void addNavigationRequest(@RequestBody NavigationRequestDto requestDto) {
        navigationRequestService.saveNavigationRequest(requestDto);
    }

    @GetMapping("/route-requests")
    public NavigationRequestListDto getNavigationRequests(Principal principal) {
        return new NavigationRequestListDto(navigationRequestService.getNavigationRequests(principal.getName()));
    }

    @GetMapping("/velocity-intervals")
    public VelocityIntervalListDto getVelocityIntervals() {
        return new VelocityIntervalListDto(velocityIntervalService.getVelocityIntervals());
    }

    @SneakyThrows
    @PutMapping("/integral-velocities")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void resetIntegralVelocities(@RequestParam MultipartFile file) {
        velocityIntervalService.resetIntegralVelocities(file.getInputStream());
    }

    @SneakyThrows
    @PutMapping("/default-routes")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void resetDefaultRoutes() {
        defaultRouteService.createAllDefaultRoutes();
    }

}
