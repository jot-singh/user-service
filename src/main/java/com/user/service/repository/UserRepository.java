package com.user.service.repository;

import com.user.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByUsername(String username);

    User findByUsernameIgnoreCaseAndPassword(String username, String password);

    User findByEmailIgnoreCaseAndPassword(String email, String password);
}
