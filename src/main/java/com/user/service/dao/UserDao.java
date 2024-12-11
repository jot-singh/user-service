package com.user.service.dao;

import com.user.service.entity.User;

public interface UserDao {
    void save(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByUsernameAndPassword(String username, String password);

    User findByEmailAndPassword(String email, String password);

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
