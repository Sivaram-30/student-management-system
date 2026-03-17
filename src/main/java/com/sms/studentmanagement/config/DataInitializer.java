package com.sms.studentmanagement.config;

import com.sms.studentmanagement.entity.User;
import com.sms.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Create Admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .email("admin@sms.com")
                .password(passwordEncoder.encode("admin123"))
                .roles(Set.of(User.Role.ROLE_ADMIN))
                .build();
            userRepository.save(admin);
            System.out.println(
                "✅ Admin created: admin / admin123");
        }

        // Create Teacher user
        if (!userRepository.existsByUsername("teacher")) {
            User teacher = User.builder()
                .username("teacher")
                .email("teacher@sms.com")
                .password(passwordEncoder.encode("teacher123"))
                .roles(Set.of(User.Role.ROLE_TEACHER))
                .build();
            userRepository.save(teacher);
            System.out.println(
                "✅ Teacher created: teacher / teacher123");
        }
    }
}

