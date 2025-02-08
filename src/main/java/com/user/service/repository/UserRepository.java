package com.user.service.repository;

import com.user.service.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCaseAndPassword(String username, String password);

    Optional<User> findByEmailIgnoreCaseAndPassword(String email, String password);

    Optional<User> findById(Long id);
}
