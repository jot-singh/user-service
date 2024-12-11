package com.user.service.dao;

import com.user.service.entity.Session;

public interface SessionDao {
    void save(Session session);

    Session findByUsername(String username);

    Session findByToken(String token);

    Session findByTokenAndUsername(String token, String username);

    void delete(Session session);
}
