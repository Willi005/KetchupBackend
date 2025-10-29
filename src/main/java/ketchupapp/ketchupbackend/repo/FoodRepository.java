package ketchupapp.ketchupbackend.repo;

import ketchupapp.ketchupbackend.model.Food;
import ketchupapp.ketchupbackend.model.FoodCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FoodRepository extends MongoRepository<Food, String> {
    List<Food> findByCategory(FoodCategory category);
    List<Food> findByName(String name);
    List<Food> findByPrice(double price);
}
