package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.UserLoginDto;
import ketchupapp.ketchupbackend.dto.UserRequestDto;
import ketchupapp.ketchupbackend.dto.UserResponseDto;
import ketchupapp.ketchupbackend.model.UserRol;

import java.util.List;

public interface UserService {
    // Registro y autenticación
    UserResponseDto registerUser(UserRequestDto userRequestDto);
    UserResponseDto login(UserLoginDto loginDto);

    // CRUD
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(long id);
    UserResponseDto updateUser(long id, UserRequestDto userRequestDto);
    void deleteUser(long id);

    // Buscar por rol
    List<UserResponseDto> getUsersByRol(UserRol rol);
}
