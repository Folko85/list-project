package main.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.dto.request.LoginRequest;
import main.dto.request.RegisterRequest;
import main.dto.response.LoginResponse;
import main.dto.response.ProfileResponse;
import main.dto.response.SuccessResponse;
import main.exception.IncorrectNiknameException;
import main.exception.UserExistException;
import main.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public SuccessResponse register(@RequestBody RegisterRequest registerRequest) throws UserExistException, IncorrectNiknameException {
        return userService.register(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('user:write')")
    public ProfileResponse getProfile(@PathVariable String username) {
        return userService.getProfile(username);
    }

    @GetMapping("/logout")
    public SuccessResponse logout() {
        return userService.logout();
    }
}
