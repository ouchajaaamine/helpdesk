package com.example.user_service.services;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.Status;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository to interact with the database
    @Autowired
    private EmailService emailService ;
    @Override
    public User submitSignUpRequest(String firstName, String lastName, String email) {
        if (firstName == null || lastName == null || email == null) {
            throw new IllegalArgumentException("First name, last name, and email cannot be null");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setStatus(Status.PENDING);
        user.setRole(Role.EMPLOYEE);

        User savedUser = userRepository.save(user);
        
        // 📧 SEND EMAIL TO ADMIN ABOUT NEW REGISTRATION

       /* try {
            emailService.sendNewUserRegistrationToAdmin(savedUser);
            System.out.println("✅ Admin notification sent for user: " + savedUser.getEmail());

        } catch (Exception e) {
            System.err.println("⚠️ Email notification failed, but user registration completed: " + e.getMessage());
        } */

        return savedUser;
    }
    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(Status.PENDING);
    }
    @Override
    public User getUserById(long id) {
        return userRepository.getById(id);
    }
    @Override
    public User getPendingUserById(long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (user.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("User with id: " + id + " is not in PENDING status");
        }
        
        return user;
    }

    
@Override
public List<User> getActiveUsers() {
    return userRepository.findByStatus(Status.ACTIVE);
}

    @Override
    public User approveUser(long id) {

            User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            
            if (!user.getStatus().equals(Status.PENDING)) {
                throw new IllegalStateException("User is not pending approval");
            }

            user.setUsername(generateUsername(user));
            user.setPassword(generateTemporaryPassword());
            user.setStatus(Status.ACTIVE);
            user.setApprovalDate(LocalDateTime.now());
    
            User approvedUser = userRepository.save(user);

            //email 
            try {
                emailService.sendApprovalNotification(approvedUser);
                        System.out.println("✅ Approval email sent to: " + approvedUser.getEmail());

            } catch (Exception e) {
        System.err.println("⚠️ Failed to send approval email: " + e.getMessage());
            }

            return approvedUser;

    }







    // Helper methods
    private String generateUsername(User user) {
    return (user.getFirstName() + "." + user.getLastName()).toLowerCase();
}

private String generateTemporaryPassword() {
    return "temp" + System.currentTimeMillis();
}
@Override
public List<User> getAllUsers() {
return userRepository.findAll();
}


    
}
