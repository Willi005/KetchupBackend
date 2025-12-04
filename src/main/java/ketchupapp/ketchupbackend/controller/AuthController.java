package ketchupapp.ketchupbackend.controller;

import ketchupapp.ketchupbackend.dto.UserLoginDto;
import ketchupapp.ketchupbackend.dto.UserResponseDto;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.User;
import ketchupapp.ketchupbackend.repo.UserRepository;
import ketchupapp.ketchupbackend.security.JwtService;
import ketchupapp.ketchupbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins="http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService; // Para obtener el DTO de respuesta limpio

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        List<User> users = userRepository.findByUsername(request.getUsername());
        if(users.isEmpty()) throw new ResourceNotFoundException("Usuario no encontrado.");

        User user = users.get(0);

        // Custom User Details implementation map
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of() // Roles handled internally
        );

        String jwtToken = jwtService.generateToken(userDetails);
        UserResponseDto userDto = userService.getUserById(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("user", userDto);

        return ResponseEntity.ok(response);
    }
}