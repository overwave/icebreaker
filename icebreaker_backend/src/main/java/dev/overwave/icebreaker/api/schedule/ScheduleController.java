package dev.overwave.icebreaker.api.schedule;


import dev.overwave.icebreaker.core.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/icebreaker/api/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PutMapping("/schedules")
    public void createSchedule() {
        scheduleService.createSchedule();
    }
}
