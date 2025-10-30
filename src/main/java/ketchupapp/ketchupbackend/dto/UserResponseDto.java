package ketchupapp.ketchupbackend.dto;

import ketchupapp.ketchupbackend.model.UserRol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record UserResponseDto(String id,
                              String username,
                              String name,
                              String secondName,
                              String rut,
                              UserRol rol) {

}
