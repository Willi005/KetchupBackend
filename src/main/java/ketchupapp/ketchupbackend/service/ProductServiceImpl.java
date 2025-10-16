package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("productService")

public class ProductServiceImpl {

    public List<Product> getProducts() {
        String imageTemplate = "https://placehold.co/50x50/png";
        List<Product> products = new ArrayList<>(Arrays.asList(
                new Product("123", "Mac Big", 7.99, 5,imageTemplate),
                new Product("321", "World Classic", 6.99, 10,imageTemplate),
                new Product("132", "Papas XL", 4.99, 20,imageTemplate)
        ));
        return products;
    }

}
