package com.example.user_service.services;

import java.util.List;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserStats;

public interface UserService {

    User submitSignUpRequest(String firstName, String lastName, String email, Role role);
    User getUserById(long id);

    // admin methods
    List<User> getPendingUsers();
    User getPendingUserById(long id);
    User approveUser(long id);
    List<User> getActiveUsers();
    List<User> getAllUsers();

    // profile management
    User updateProfile(Long userId, String firstName, String lastName, String email);
    User changePassword(Long userId, String newPassword);

    // admin operations
    User rejectUser(Long userId, String reason);
    User changeUserRole(Long userId, Role newRole);
    User deactivateUser(Long userId);

    // statistics and search
    UserStats getUserStats();
    List<User> searchUsers(String query);
    List<User> getUsersByRole(Role role);
}
