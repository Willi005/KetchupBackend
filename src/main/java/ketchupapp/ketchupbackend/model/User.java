package ketchupapp.ketchupbackend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//lombok genera automáticamente getters y setters y constructores, es dependencia :)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String name;
    private String secondName;
    private String rut;
    private UserRol rol;
    private String password;


}
