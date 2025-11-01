package ketchupapp.ketchupbackend.repo;

import ketchupapp.ketchupbackend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    //Para pruebas de dashboard futuras en el front
    List<Order> findAllByOrderTimestampBetween(LocalDateTime start, LocalDateTime end);
}
