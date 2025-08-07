package com.example.user_service.services;

import java.util.List;

import com.example.user_service.entity.User;

public interface UserService {

    User submitSignUpRequest(String firstName, String lastName, String email);
    User getUserById(long id);

    // admin methods
    List<User> getPendingUsers();
    User getPendingUserById(long id);
    User approveUser(long id);
    List<User> getActiveUsers();
    List<User> getAllUsers();


}
