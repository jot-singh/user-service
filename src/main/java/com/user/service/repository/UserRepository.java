package com.user.service.repository;

import com.user.service.entity.User;
import com.user.service.entity.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCaseAndPassword(String username, String password);

    Optional<User> findByEmailIgnoreCaseAndPassword(String email, String password);

    Optional<User> findById(Long id);
    
    // Admin methods for role-based access control
    
    List<User> findByRole(Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.accountLocked = false AND u.emailVerified = true")
    long countByAccountLockedFalseAndEmailVerifiedTrue();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.accountLocked = true")
    long countByAccountLockedTrue();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
}
