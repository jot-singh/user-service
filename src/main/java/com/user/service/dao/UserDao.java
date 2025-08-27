package com.user.service.dao;

import java.util.List;
import java.util.Optional;

import com.user.service.entity.User;
import com.user.service.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDao {
    void save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findById(Long id);
    
    // Admin methods for role-based access control
    void delete(User user);
    
    List<User> findAll();
    
    Page<User> findAll(Pageable pageable);
    
    List<User> findByRole(Role role);
    
    long count();
    
    long countByAccountLockedFalseAndEmailVerifiedTrue();
    
    long countByAccountLockedTrue();
    
    long countByRole(Role role);
}
