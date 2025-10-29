package ketchupapp.ketchupbackend.dto;

import ketchupapp.ketchupbackend.model.FoodCategory;

public record FoodResponseDto(String id,
                              String name,
                              double price,
                              FoodCategory category,
                              int stock,
                              String image) {

}
