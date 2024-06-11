package dev.overwave.icebreaker.api.user;

import dev.overwave.icebreaker.core.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "/icebreaker/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<LoginStatus> registerUser(@RequestBody RegisterUserRequestDto requestDto) {
        userService.registerUser(requestDto.login(), requestDto.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(LoginStatus.SUCCESS);
    }

    @GetMapping("/me")
    public UserDto selfInfo(Principal principal) {
        return userService.selfInfo(principal.getName());
    }
}