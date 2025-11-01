package ketchupapp.ketchupbackend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//lombok genera automáticamente getters y setters y constructores, es dependencia :)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "foods")
public class Food {
    @Id
    private String id;

    private String name;
    private double price;
    private FoodCategory category;
    private int stock;
    private String image;
}
