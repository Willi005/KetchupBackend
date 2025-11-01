package ketchupapp.ketchupbackend.controller;

import ketchupapp.ketchupbackend.dto.FoodPatchDto;
import ketchupapp.ketchupbackend.dto.FoodRequestDto;
import ketchupapp.ketchupbackend.dto.FoodResponseDto;
import ketchupapp.ketchupbackend.model.FoodCategory;
import ketchupapp.ketchupbackend.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
@CrossOrigin(origins="http://localhost:5173")
public class FoodController {

    private final FoodService foodService;

    public FoodController(@Qualifier("foodService") FoodService foodService) {
        this.foodService = foodService;
    }

    // POST /food
    @PostMapping
    public ResponseEntity<FoodResponseDto> createFood(@Valid @RequestBody FoodRequestDto foodRequest) {
        FoodResponseDto newFood = foodService.createFood(foodRequest);
        return new ResponseEntity<>(newFood, HttpStatus.CREATED); // 201 Created
    }

    // GET /food
    @GetMapping
    public ResponseEntity<List<FoodResponseDto>> getAllFood() {
        List<FoodResponseDto> products = foodService.getAllFood();
        return ResponseEntity.ok(products); // 200 OK
    }

    // GET /food/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDto> getFoodById(@PathVariable String id) {
        FoodResponseDto food = foodService.getFoodById(id);
        return ResponseEntity.ok(food); // 200 OK
    }

    // GET /food/category/{categoryName}
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<FoodResponseDto>> getFoodByCategory(@PathVariable String categoryName) {
        // Convertimos el String a Enum de forma segura ya que Mongo lo trata como String
        FoodCategory category = FoodCategory.valueOf(categoryName.toUpperCase());
        List<FoodResponseDto> foods = foodService.getFoodByCategory(category);
        return ResponseEntity.ok(foods);
    }

    // PUT /food/{id}
    @PutMapping("/{id}")
    public ResponseEntity<FoodResponseDto> updateFood(@PathVariable String id, @Valid @RequestBody FoodRequestDto foodRequest) {
        FoodResponseDto updatedFood = foodService.updateFood(id, foodRequest);
        return ResponseEntity.ok(updatedFood); // 200 OK
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FoodResponseDto> patchFood(@PathVariable String id, @RequestBody FoodPatchDto foodPatchRequest) {
        FoodResponseDto updatedFood = foodService.patchFood(id, foodPatchRequest);
        return ResponseEntity.ok(updatedFood); // 200 OK
    }
    // DELETE /food/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable String id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}