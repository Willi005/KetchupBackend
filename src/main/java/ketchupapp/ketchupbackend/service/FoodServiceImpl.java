package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.FoodPatchDto;
import ketchupapp.ketchupbackend.dto.FoodRequestDto;
import ketchupapp.ketchupbackend.dto.FoodResponseDto;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.Food;
import ketchupapp.ketchupbackend.model.FoodCategory;
import ketchupapp.ketchupbackend.repo.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("foodService")
public class FoodServiceImpl implements FoodService {
    private final FoodRepository foodRepository;

    @Autowired
    public FoodServiceImpl(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    //CREATE
    @Override
    public FoodResponseDto createFood(FoodRequestDto foodRequestDto) {
        Food food = mapToEntity(foodRequestDto);
        Food savedFood = foodRepository.save(food);
        return mapToResponseDto(savedFood);
    }

    //READ
    @Override
    public List<FoodResponseDto> getAllFood() {
        return foodRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public FoodResponseDto getFoodById(String id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con id: " + id));
        return mapToResponseDto(food);
    }

    @Override
    public List<FoodResponseDto> getFoodByCategory(FoodCategory category) {
        return foodRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    //UPDATE
    @Override
    public FoodResponseDto updateFood(String id, FoodRequestDto foodRequestDto) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con id: " + id));

        existingFood.setName(foodRequestDto.getName());
        existingFood.setPrice(foodRequestDto.getPrice());
        existingFood.setCategory(foodRequestDto.getCategory());
        existingFood.setStock(foodRequestDto.getStock());
        existingFood.setImage(foodRequestDto.getImage());

        Food updatedFood = foodRepository.save(existingFood);
        return mapToResponseDto(updatedFood);
    }

    @Override
    public FoodResponseDto patchFood(String id, FoodPatchDto patchDto) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con id: " + id));

        if (patchDto.getName() != null) {
            existingFood.setName(patchDto.getName());
        }
        if (patchDto.getPrice() != null) {
            existingFood.setPrice(patchDto.getPrice());
        }
        if (patchDto.getCategory() != null) {
            existingFood.setCategory(patchDto.getCategory());
        }
        if (patchDto.getStock() != null) {
            existingFood.setStock(patchDto.getStock());
        }
        if (patchDto.getImage() != null) {
            existingFood.setImage(patchDto.getImage());
        }

        Food updatedFood = foodRepository.save(existingFood);
        return mapToResponseDto(updatedFood);
    }

    // DELETE
    @Override
    public void deleteFood(String id) {
        if (!foodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comida no encontrada con id: " + id);
        }
        foodRepository.deleteById(id);
    }

    //MAPPERS
    // Convierte Entidad (BD) a DTO de Respuesta (API)
    private FoodResponseDto mapToResponseDto(Food food) {
        return new FoodResponseDto(
                food.getId(),
                food.getName(),
                food.getPrice(),
                food.getCategory(),
                food.getStock(),
                food.getImage()
        );
    }

    // Convierte DTO de Petición (API) a Entidad (BD)
    private Food mapToEntity(FoodRequestDto dto) {
        Food food = new Food();
        food.setName(dto.getName());
        food.setPrice(dto.getPrice());
        food.setCategory(dto.getCategory());
        food.setStock(dto.getStock());
        food.setImage(dto.getImage());
        return food;
    }
}