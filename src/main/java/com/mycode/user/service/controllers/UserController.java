package com.mycode.user.service.controllers;

import com.mycode.user.service.entities.User;
import com.mycode.user.service.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
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

    int retryCount = 1;

    @GetMapping("{id}")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
    //@Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback")
    //@CircuitBreaker(name = "ratingHotelBreaker",fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> findUserById(@PathVariable String id){

        System.out.println("Retry count: " + retryCount);
        retryCount++;
        User user = userService.getUser(id);

        return ResponseEntity.ok(user);
     }

     public ResponseEntity<User> ratingHotelFallback(String id, Exception exception){
         System.out.println("Fallback is executed");
        exception.printStackTrace();
         User user = User.builder().email("dummy@gmail.com")
                 .name("Dummy")
                 .about("This User is created dummy because some service is down")
                 .userId("bxhsqjw1234mndxbju12")
                 .build();

         return new ResponseEntity<>(user, HttpStatus.OK);
     }

     @GetMapping
     public ResponseEntity<List<User>> findAll(){
         List<User> allUser = userService.getAllUser();

         return ResponseEntity.ok(allUser);
     }
}
