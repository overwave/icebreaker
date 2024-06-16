package dev.overwave.icebreaker.api.schedule;


import dev.overwave.icebreaker.api.navigation.NavigationRequestController;
import dev.overwave.icebreaker.api.navigation.NavigationRequestsDtoWithRoute;
import dev.overwave.icebreaker.core.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "/icebreaker/api/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final NavigationRequestController navigationRequestController;

    @PutMapping("/schedules")
    @PreAuthorize("hasAuthority('ADMIN')")
    public NavigationRequestsDtoWithRoute createSchedule(Principal principal) {
        scheduleService.createSchedule();
        return navigationRequestController.getNavigationRequests(principal);
    }
}
