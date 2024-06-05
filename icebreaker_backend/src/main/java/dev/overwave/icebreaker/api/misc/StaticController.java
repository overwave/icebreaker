package dev.overwave.icebreaker.api.misc;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticController {

    @RequestMapping("/icebreaker/**")
    public String asd(HttpServletRequest request) {
        String path = StringUtils.substringAfter(request.getServletPath(), "/icebreaker");

        if (path.contains(".")) {
            return path;
        }
        return path.isEmpty() ? "index.html" : path + ".html";
    }
}
