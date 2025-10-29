package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.FoodPatchDto;
import ketchupapp.ketchupbackend.dto.FoodRequestDto;
import ketchupapp.ketchupbackend.dto.FoodResponseDto;
import ketchupapp.ketchupbackend.model.Food;
import ketchupapp.ketchupbackend.model.FoodCategory;

import java.util.List;

public interface FoodService {
    //CREATE
    FoodResponseDto createFood(FoodRequestDto foodRequestDto);

    //READ
    List <FoodResponseDto> getAllFood();
    FoodResponseDto getFoodById(String id);
    List <FoodResponseDto> getFoodByCategory(FoodCategory category);

    //UPDATE
    FoodResponseDto updateFood(String id, FoodRequestDto foodRequestDto);

    //PATCH
    FoodResponseDto patchFood(String id, FoodPatchDto foodPatchDto);

    //DELETE
    void deleteFood(String id);
}
