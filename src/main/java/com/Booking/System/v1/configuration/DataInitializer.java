package com.booking.system.v1.configuration;

import com.booking.system.v1.entity.Role;
import com.booking.system.v1.entity.User;
import com.booking.system.v1.entity.UserStatus;
import com.booking.system.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

//creates an admin if none exists
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.findByEmail("admin@bookingsystem.com").isEmpty()) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@bookingsystem.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);

            userRepository.save(admin);

            System.out.println("Admin user created: admin@bookingsystem.com / admin123");
        }
    }
}