package com.user.service.dao;

import java.util.Optional;

import com.user.service.entity.User;

public interface UserDao {
    void save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findById(Long id);

//    void delete(User user);
//
//    void update(User user);
//
//    List<User> findAll();
//
//    User findById(String id);
//
//    void deleteAll();

}
