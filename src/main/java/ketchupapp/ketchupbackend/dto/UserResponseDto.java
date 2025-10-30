package ketchupapp.ketchupbackend.dto;

import ketchupapp.ketchupbackend.model.UserRol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String username;
    private String name;
    private String secondName;
    private String rut;
    private UserRol rol;
}
