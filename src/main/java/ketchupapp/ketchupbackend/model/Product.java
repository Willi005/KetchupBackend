package ketchupapp.ketchupbackend.model;
import lombok.*;
//lombok genera automaticamente getters y setters y constructores, es depndencia :)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Product {
    private String id;
    private String name;
    private double price;
    private ProductCategory category;
    private int stock;
    private String imageUrl;


}
