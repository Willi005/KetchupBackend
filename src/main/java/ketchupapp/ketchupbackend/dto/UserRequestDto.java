package ketchupapp.ketchupbackend.dto;

import jakarta.validation.constraints.NotBlank;
import ketchupapp.ketchupbackend.model.UserRol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String secondName;

    @NotBlank
    private String rut;
    private UserRol rol;

    @NotBlank
    private String password;
}
