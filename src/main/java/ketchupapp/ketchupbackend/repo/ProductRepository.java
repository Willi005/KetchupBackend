package ketchupapp.ketchupbackend.repo;

import ketchupapp.ketchupbackend.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findByName(String name);
    List<Product> findByPrice(double price);
}
