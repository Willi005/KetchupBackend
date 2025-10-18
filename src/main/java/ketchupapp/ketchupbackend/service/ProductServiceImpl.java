package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.model.Product;
import ketchupapp.ketchupbackend.model.ProductCategory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("productService")

public class ProductServiceImpl {

    public List<Product> getProducts() {
        String imageTemplate = "https://www.hola.com/horizon/landscape/aab0370ef079-burguer-pollo-adob-t.jpg";
        List<Product> products = new ArrayList<>(Arrays.asList(
                new Product("123", "Mac Big Burger", 7600, ProductCategory.BURGER, 21, imageTemplate),
                new Product("321", "World Classic Burger", 7200, ProductCategory.BURGER, 16, imageTemplate),
                new Product("132", "Beacon Burger Mix", 8000, ProductCategory.BURGER, 18, imageTemplate),
                new Product("231", "Terminator Burger", 12000, ProductCategory.BURGER, 20, imageTemplate),
                new Product("412", "Super Fries XL", 1600, ProductCategory.FRIE, 21, imageTemplate),
                new Product("324", "Chilean Hot Dog", 4200, ProductCategory.HOTDOG, 16, imageTemplate),
                new Product("164", "Simple Hot Dog", 3000, ProductCategory.HOTDOG, 18, imageTemplate),
                new Product("212", "Pepperoni pizza whit pineapple", 15000, ProductCategory.PIZZA, 20, imageTemplate)
        ));
        return products;
    }

}
