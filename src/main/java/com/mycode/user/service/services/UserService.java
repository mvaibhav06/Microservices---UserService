package com.mycode.user.service.services;

import com.mycode.user.service.entities.User;

import java.util.List;

public interface UserService {

    User save(User user);

    List<User> getAllUser();

    User getUser(String userId);


}
