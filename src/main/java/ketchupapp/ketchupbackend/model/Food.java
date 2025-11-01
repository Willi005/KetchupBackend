package ketchupapp.ketchupbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//lombok genera automáticamente getters y setters y constructores, es dependencia :)
@Data
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
