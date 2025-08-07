package com.example.user_service.services;

import com.example.user_service.entity.User;


public interface EmailService {

    // 1. ESSENTIAL: Notify admin of new signup
    void sendNewUserRegistrationToAdmin(User user);
    
    // 2. ESSENTIAL: Notify user of approval/rejection
    void sendApprovalNotification(User user);
    void sendRejectionNotification(User user, String reason);


}
