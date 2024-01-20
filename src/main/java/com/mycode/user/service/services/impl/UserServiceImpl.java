package com.mycode.user.service.services.impl;

import com.mycode.user.service.entities.Hotel;
import com.mycode.user.service.entities.Rating;
import com.mycode.user.service.entities.User;
import com.mycode.user.service.exceptions.ResourceNotFoundException;
import com.mycode.user.service.external.services.HotelService;
import com.mycode.user.service.repositories.UserRepository;
import com.mycode.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User save(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        User user =  userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on the server !!: " + userId));
//      http://localhost:8083/ratings/users/e858fed5-9b73-43d5-94d9-c482caae8caa

        Rating[] ratings = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+userId, Rating[].class);
        logger.info("{} ",ratings);

        List<Rating> rating_list = Arrays.stream(ratings).toList();

        List<Rating> ratingList = rating_list.stream().map(rating -> {
            //http://localhost:8082/hotels/95d4df5b-9fa9-4e07-8d66-d9ce8b430730
            //ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            //logger.info("response status code: {} ", forEntity.getStatusCode());
            rating.setHotel(hotel);

            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);

        return user;
    }
}
