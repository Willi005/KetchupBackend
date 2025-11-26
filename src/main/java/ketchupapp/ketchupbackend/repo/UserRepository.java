package ketchupapp.ketchupbackend.repo;

import ketchupapp.ketchupbackend.model.User;
import ketchupapp.ketchupbackend.model.UserRol;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByName(String name);
    List<User> findByRol(UserRol rol);
    List<User> findByUsername(String username);
    List<User> findByRut(String rut);

    // este metodo retorna usuario se usa para las clases de scurity ya que el findbyusername devuelve una lista de usuario cuando deberia devolver usuario
    //pero preferi hacer otro metodo para no interferir en todo el proyecto ya que estos tienen su uso.
    Optional<User> findFirstByUsername(String username);
}
//usar username en base datos y ver que ondas postman pq busca por el nombre y no por el username probrar endpoint