package ketchupapp.ketchupbackend.controller;

import ketchupapp.ketchupbackend.model.Product;
import ketchupapp.ketchupbackend.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins="http://localhost:5173")
public class ProductController {

    @Autowired
    @Qualifier("productService")


    private ProductServiceImpl productService;

    @GetMapping
    public ResponseEntity<?> getListOfProducts() {
        List <Product> products = productService.getProducts();
        return ResponseEntity.ok(products);
    }


}
