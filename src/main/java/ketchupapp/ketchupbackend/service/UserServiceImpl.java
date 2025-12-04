package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.UserLoginDto;
import ketchupapp.ketchupbackend.dto.UserRequestDto;
import ketchupapp.ketchupbackend.dto.UserResponseDto;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.User;
import ketchupapp.ketchupbackend.model.UserRol;
import ketchupapp.ketchupbackend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // @Lazy para evitar dependencia circular si SecurityConfig usa UserService
    @Autowired
    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTRO (Actualizado con encriptación)
    @Override
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        User user = mapToEntity(userRequestDto);
        // ENCRIPTAMOS LA CONTRASEÑA ANTES DE GUARDAR
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    // LOGIN (Deprecado, usar AuthController para el flow real con Token)
    // Se mantiene por compatibilidad si lo necesitas para tests rápidos sin seguridad
    @Override
    public UserResponseDto login(UserLoginDto loginDto) {
        List<User> users = userRepository.findByUsername(loginDto.getUsername());
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        User user = users.get(0);
        // Validamos contraseña encriptada
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        return mapToResponseDto(user);
    }

    // READ
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return mapToResponseDto(user);
    }

    // UPDATE
    @Override
    public UserResponseDto updateUser(String id, UserRequestDto dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        existingUser.setUsername(dto.getUsername());
        existingUser.setName(dto.getName());
        existingUser.setSecondName(dto.getSecondName());
        existingUser.setRut(dto.getRut());
        existingUser.setRol(dto.getRol());

        // Si viene password nuevo, lo encriptamos
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return mapToResponseDto(updatedUser);
    }

    // DELETE
    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    // FILTRO POR ROL
    @Override
    public List<UserResponseDto> getUsersByRol(UserRol rol) {
        return userRepository.findByRol(rol)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // MAPPERS
    private UserResponseDto mapToResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSecondName(),
                user.getRut(),
                user.getRol()
        );
    }

    private User mapToEntity(UserRequestDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSecondName(dto.getSecondName());
        user.setRut(dto.getRut());
        user.setRol(dto.getRol());
        // El password se setea encriptado en el método register
        return user;
    }
}