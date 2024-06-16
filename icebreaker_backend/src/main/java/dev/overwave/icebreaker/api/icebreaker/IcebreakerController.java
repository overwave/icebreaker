package dev.overwave.icebreaker.api.icebreaker;

import dev.overwave.icebreaker.api.navigation.ShipRouteDto;
import dev.overwave.icebreaker.core.navigation.NavigationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/icebreaker/api/icebreaker", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class IcebreakerController {
    private final NavigationRequestService navigationRequestService;

    @GetMapping("/all")
    public IcebreakerDetailListDto getShipRoutes() {
        return navigationRequestService.getIcebreakersDetails();
    }
    @GetMapping("/route")
    public List<ShipRouteDto> getShipRoutes(@RequestParam long icebreakerId) {
        return navigationRequestService.getShipRouteByRequestId(icebreakerId);
    }
}
