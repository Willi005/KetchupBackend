package ketchupapp.ketchupbackend.dto;

import ketchupapp.ketchupbackend.model.FoodCategory;
import lombok.Data;

@Data
public class FoodPatchDto {
    private String name;
    private Double price;
    private FoodCategory category;
    private Integer stock;
    private String image;
}