package ketchupapp.ketchupbackend.config.security.controller;

import ketchupapp.ketchupbackend.config.security.basic.UserDetailsServiceImplent;
import ketchupapp.ketchupbackend.config.security.controller.dto.AuthLoginRequest;
import ketchupapp.ketchupbackend.dto.UserRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserDetailsServiceImplent userDetailService;

    @PostMapping("/sign-up")
    public ResponseEntity<Object> register(@RequestBody @Valid UserRequestDto userRequest) {
        try {
            return new ResponseEntity<>(this.userDetailService.createUser(userRequest), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage(), "status", false), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/log-in")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthLoginRequest userRequest) {
        try {
            return new ResponseEntity<>(this.userDetailService.loginUser(userRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Credenciales incorrectas", "error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}