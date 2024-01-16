package com.mycode.user.service.controllers;

import com.mycode.user.service.entities.User;
import com.mycode.user.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){

        User savedUser = userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("{id}")
     public ResponseEntity<User> findUserById(@PathVariable String id){
        User user = userService.getUser(id);

        return ResponseEntity.ok(user);
     }

     @GetMapping
     public ResponseEntity<List<User>> findAll(){
         List<User> allUser = userService.getAllUser();

         return ResponseEntity.ok(allUser);
     }
}
