package ketchupapp.ketchupbackend.model;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Product {
    private String id;
    private String name;
    private double price;
    private int stock;
    private String imageUrl;


}
