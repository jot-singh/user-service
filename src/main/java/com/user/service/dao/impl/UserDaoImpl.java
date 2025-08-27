package com.user.service.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.user.service.dao.UserDao;
import com.user.service.entity.User;
import com.user.service.entity.Role;
import com.user.service.repository.UserRepository;

@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameIgnoreCaseAndPassword(username, password);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailIgnoreCaseAndPassword(email, password);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // Admin methods for role-based access control
    
    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    public long count() {
        return userRepository.count();
    }
    
    @Override
    public long countByAccountLockedFalseAndEmailVerifiedTrue() {
        return userRepository.countByAccountLockedFalseAndEmailVerifiedTrue();
    }
    
    @Override
    public long countByAccountLockedTrue() {
        return userRepository.countByAccountLockedTrue();
    }
    
    @Override
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
