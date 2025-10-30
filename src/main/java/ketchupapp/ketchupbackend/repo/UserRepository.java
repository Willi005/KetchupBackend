package ketchupapp.ketchupbackend.repo;

import ketchupapp.ketchupbackend.model.User;
import ketchupapp.ketchupbackend.model.UserRol;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByName(String name);
    List<User> findByRol(UserRol rol);
    List<User> findByUsername(String username);
    List<User> findByRut(String rut);
}
//usar username en base datos y ver que ondas postman pq busca por el nombre y no por el username probrar endpoint