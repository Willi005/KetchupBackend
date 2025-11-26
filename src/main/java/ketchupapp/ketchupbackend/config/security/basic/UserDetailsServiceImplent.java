package ketchupapp.ketchupbackend.config.security.basic;

import ketchupapp.ketchupbackend.config.security.controller.dto.AuthCreateResponse;
import ketchupapp.ketchupbackend.config.security.controller.dto.AuthLoginRequest;
import ketchupapp.ketchupbackend.config.security.controller.dto.AuthResponse;
import ketchupapp.ketchupbackend.config.security.utils.JwtUtil;
import ketchupapp.ketchupbackend.dto.UserRequestDto;
import ketchupapp.ketchupbackend.model.User;
import ketchupapp.ketchupbackend.repo.UserRepository;
import ketchupapp.ketchupbackend.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImplent implements UserDetailsService {
    private final JwtUtil jwtUtils;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserDetailsServiceImplent(JwtUtil jwtUtils,
                                     UserRepository repository,
                                     @Lazy PasswordEncoder passwordEncoder,
                                     @Lazy UserService userService) {
        this.jwtUtils = jwtUtils;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findFirstByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("The user has not been found"));
    }

    public AuthCreateResponse createUser(UserRequestDto userRequest) {
        userService.registerUser(userRequest);
        return new AuthCreateResponse("The user has been created successfully", true);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();
        String accessToken;

        Optional<User> userOptional = repository.findFirstByUsername(username);

        if (userOptional.isPresent()) {
            Authentication authentication = this.authenticate(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            accessToken = jwtUtils.createToken(authentication);
            return new AuthResponse("successful validation", accessToken);
        } else {
            throw new BadCredentialsException("User not found");
        }
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        validateUserDetails(userDetails);
        validatePassword(userDetails, password);

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }

    private void validateUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("Username and password are required");
        }
    }

    private void validatePassword(UserDetails userDetails, String password) {
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }
    }
}