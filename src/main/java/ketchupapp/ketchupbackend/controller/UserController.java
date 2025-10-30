 // endpoint login
 // endpoint registro
 //listar x rol
 // metodosCruds
 package ketchupapp.ketchupbackend.controller;

 import ketchupapp.ketchupbackend.dto.UserLoginDto;
 import ketchupapp.ketchupbackend.dto.UserRequestDto;
 import ketchupapp.ketchupbackend.dto.UserResponseDto;
 import ketchupapp.ketchupbackend.model.UserRol;
 import ketchupapp.ketchupbackend.service.UserService;
 import jakarta.validation.Valid;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Qualifier;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;

 import java.util.List;

 @RestController
 @RequestMapping("/users")
 @CrossOrigin(origins="http://localhost:5173")
 public class UserController {

     @Autowired
     @Qualifier("userService")
     private UserService userService;

     // LOGIN cambiar
     @PostMapping("/login")
     public ResponseEntity<UserResponseDto> login(@Valid @RequestBody UserLoginDto loginDto) {
         UserResponseDto user = userService.login(loginDto);
         return ResponseEntity.ok(user);
     }

     // REGISTRO
     @PostMapping("/register")
     public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto userRequest) {
         UserResponseDto newUser = userService.registerUser(userRequest);
         return new ResponseEntity<>(newUser, HttpStatus.CREATED);
     }

     // LISTAR TODOS
     @GetMapping
     public ResponseEntity<List<UserResponseDto>> getAllUsers() {
         List<UserResponseDto> users = userService.getAllUsers();
         return ResponseEntity.ok(users);
     }



     // LISTAR POR ROL
     @GetMapping("/rol/{rol}")
     public ResponseEntity<List<UserResponseDto>> getUsersByRol(@PathVariable String rol) {
         UserRol roleEnum = UserRol.valueOf(rol.toUpperCase());
         List<UserResponseDto> users = userService.getUsersByRol(roleEnum);
         return ResponseEntity.ok(users);
     }

     // ACTUALIZAR
     @PutMapping("/{id}")
     public ResponseEntity<UserResponseDto> updateUser(@PathVariable long id, @Valid @RequestBody UserRequestDto userRequest) {
         UserResponseDto updatedUser = userService.updateUser(id, userRequest);
         return ResponseEntity.ok(updatedUser);
     }

     // ELIMINAR
     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteUser(@PathVariable long id) {
         userService.deleteUser(id);
         return ResponseEntity.noContent().build();
     }

     // LISTAR POR ID
     @GetMapping("/{id}")
     public ResponseEntity<UserResponseDto> getUserById(@PathVariable long id) {
         UserResponseDto user = userService.getUserById(id);
         return ResponseEntity.ok(user);
     }
 }

