package com.example.user_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.user_service.entity.User;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendNewUserRegistrationToAdmin(User user) {
            try{
                SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo("amine.elksiba@gmail.com");
                     message.setSubject("🔔 New User Registration - " + user.getFirstName() + " " + user.getLastName());
                    message.setText(buildAdminNotificationText(user));

                    mailSender.send(message);

            } catch (Exception e){
            System.err.println("❌ Failed to send admin notification: " + e.getMessage());
            }

    }

    @Override
    public void sendApprovalNotification(User user) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
         message.setSubject("🎉 Account Approved - Welcome to HelpDesk System!");
            message.setText(buildApprovalText(user));
            
            mailSender.send(message);
            System.out.println("✅ Approval notification sent to: " + user.getEmail());
            
    } catch (Exception e) {
            System.err.println("❌ Failed to send approval notification: " + e.getMessage());
    }   
    
    
    }

    @Override
    public void sendRejectionNotification(User user, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
             message.setTo(user.getEmail());
            message.setSubject("❌ Account Registration - Update Required");
            message.setText(buildRejectionText(user, reason));
            
            mailSender.send(message);
            System.out.println("✅ Rejection notification sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send rejection notification: " + e.getMessage());
        }
    }

     // Helper method to build admin notification text
    private String buildAdminNotificationText(User user) {
        return String.format(
            "Hello Admin,\n\n" +
            "A new user has registered for the HelpDesk system:\n\n" +
            "👤 Name: %s %s\n" +
            "📧 Email: %s\n" +
            "📅 Registration Date: %s\n" +
            "🔄 Status: %s\n\n" +
            "Please log in to the admin panel to review and approve/reject this registration.\n\n" +
            "Best regards,\n" +
            "HelpDesk System",
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getStatus()
        );
    }

     // Helper method to build approval text
    private String buildApprovalText(User user) {
        return String.format(
            "Hello %s,\n\n" +
            "Great news! Your account has been approved! 🎉\n\n" +
            "Account Details:\n" +
            "👤 Name: %s %s\n" +
            "📧 Email: %s\n" +
            "🆔 Username: %s\n" +
            "🔐 Temporary Password: %s\n\n" +
            "You can now log in to the HelpDesk system using your credentials.\n" +
            "Please change your password after first login.\n\n" +
            "Welcome to the team!\n\n" +
            "Best regards,\n" +
            "HelpDesk Admin Team",
            user.getFirstName(),                                                    // Hello %s
            user.getFirstName(), user.getLastName(),                              // Name: %s %s
            user.getEmail(),                                                       // Email: %s
            user.getUsername() != null ? user.getUsername() : "Will be assigned", // Username: %s
            user.getPassword() != null ? user.getPassword() : "Will be assigned"  // Password: %s
        );
    }

     // Helper method to build rejection text
    private String buildRejectionText(User user, String reason) {
        return String.format(
            "Hello %s,\n\n" +
            "Thank you for your interest in joining our HelpDesk system.\n\n" +
            "Unfortunately, we cannot approve your registration at this time.\n\n" +
            "Reason: %s\n\n" +
            "If you believe this is an error or would like to discuss this decision, " +
            "please contact our support team.\n\n" +
            "Best regards,\n" +
            "HelpDesk Admin Team",
            user.getFirstName(),
            reason != null ? reason : "Please contact admin for details"
        );
    }

    
    

}
