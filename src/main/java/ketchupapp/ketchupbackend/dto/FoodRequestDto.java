package ketchupapp.ketchupbackend.dto;

import ketchupapp.ketchupbackend.model.FoodCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FoodRequestDto {
    @NotBlank(message = "El nombre no puede ser vacío.")
    private String name;

    @PositiveOrZero(message = "El precio debe ser mayor o igual a 0.")
    private double price;

    @NotNull(message = "La categoría no puede ser nula.")
    private FoodCategory category;

    @PositiveOrZero(message = "El stock debe ser mayor o igual a 0.")
    private int stock;

    private String image;
}
