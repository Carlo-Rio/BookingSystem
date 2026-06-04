package com.booking.system.v1.repository;

import com.booking.system.v1.entity.User;
import com.booking.system.v1.entity.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User findByStatus(UserStatus status);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


}
