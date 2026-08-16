package main.service;

import lombok.AllArgsConstructor;
import main.dto.request.LoginRequest;
import main.dto.request.RegisterRequest;
import main.dto.response.LoginResponse;
import main.dto.response.ProfileResponse;
import main.dto.response.SuccessResponse;
import main.exception.IncorrectNiknameException;
import main.exception.UserExistException;
import main.model.User;
import main.model.enums.Role;
import main.repository.UserRepository;
import main.security.JwtProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Нет такого пользователя"));
        String token;
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            token = jwtProvider.generateToken(loginRequest.getUsername());
        } else throw new UsernameNotFoundException("Неверный пароль");

        return new LoginResponse().setUsername(user.getUsername()).setToken(token);

    }

    public SuccessResponse logout() {
        SecurityContextHolder.clearContext();
        return new SuccessResponse().setMessage("Вы вышли из чата");
    }

    public SuccessResponse register(RegisterRequest registerRequest) throws UserExistException, IncorrectNiknameException {
        if (!registerRequest.getUsername().matches("[\\w\\d]+")) {
            throw new IncorrectNiknameException("Никнейм может содержать только буквы и цифры");
        }
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent())
            throw new UserExistException("Пользователь с таким никнеймом уже есть");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        User user = new User().setFirstname(registerRequest.getFirstname()).setLastname(registerRequest.getLastname())
                .setUsername(registerRequest.getUsername()).setPassword(passwordEncoder.encode(registerRequest.getPassword()))
                .setRole(Role.USER);
        userRepository.save(user);
        return new SuccessResponse().setMessage("Вы успешно зарегистрированы");
    }

    public ProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Нет такого пользователя"));
        return new ProfileResponse().setFirstname(user.getFirstname())
                .setLastName(user.getLastname())
                .setUsername(user.getUsername());
    }
}
