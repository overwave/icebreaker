package dev.overwave.icebreaker.api.misc;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticController {

    @RequestMapping("/icebreaker/**")
    public String serveStaticIndex(HttpServletRequest request) {
        if (request.getServletPath().contains(".")) {
            return StringUtils.substringAfter(request.getServletPath(), "/icebreaker");
        }
        return "/index.html";
    }
}
