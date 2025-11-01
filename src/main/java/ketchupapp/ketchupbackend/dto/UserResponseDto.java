package ketchupapp.ketchupbackend.dto;

import ketchupapp.ketchupbackend.model.UserRol;

public record UserResponseDto(String id,
                              String username,
                              String name,
                              String secondName,
                              String rut,
                              UserRol rol) {

}
