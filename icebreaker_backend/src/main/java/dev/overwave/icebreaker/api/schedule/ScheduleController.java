package dev.overwave.icebreaker.api.schedule;


import dev.overwave.icebreaker.core.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.security.Principal;

@RestController
@RequestMapping(path = "/icebreaker/api/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ScheduleController {
    private static final MediaType XLSX_MEDIA_TYPE =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private final ScheduleService scheduleService;

    @PutMapping("/schedules")
    public void createSchedule() {
        scheduleService.createSchedule();
    }

    @GetMapping("/gantt")
    public ResponseEntity<Resource> getFileWithSchedule(Principal principal) {
        byte[] scheduleRaw = scheduleService.createFileWithSchedule(principal.getName());
        Resource resource = new InputStreamResource(new ByteArrayInputStream(scheduleRaw));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(XLSX_MEDIA_TYPE);
        headers.setContentDispositionFormData("attachment", "scheduleGantt.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/gantt/icebreaker")
    public ResponseEntity<Resource> getFileWithScheduleForIcebreaker(@RequestParam long icebreakerId) {
        byte[] scheduleRaw = scheduleService.createFileWithScheduleForIcebreaker(icebreakerId);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(scheduleRaw));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(XLSX_MEDIA_TYPE);
        headers.setContentDispositionFormData("attachment", "scheduleGantt.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
