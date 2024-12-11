package com.user.service.dao.impl;

import com.user.service.dao.SessionDao;
import com.user.service.entity.Session;
import com.user.service.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionDaoImpl implements SessionDao {
    @Autowired
    SessionRepository sessionRepository;

    @Override
    public void save(Session session) {
        // Save session
        sessionRepository.save(session);
    }

    @Override
    public Session findByUsername(String username) {
        // Find session by username
        return sessionRepository.findByUsername(username);
    }

    @Override
    public Session findByToken(String token) {
        // Find session by token
        return sessionRepository.findByToken(token)
                .orElse(null);
    }

    /**
     * @param token
     * @param username
     * @return
     */
    @Override
    public Session findByTokenAndUsername(String token, String username) {
        // Find session by token and username
        return sessionRepository.findByTokenAndUsername(token, username)
                .orElse(null);
    }

    @Override
    public void delete(Session session) {
        // Delete session
        sessionRepository.delete(session);
    }
}
