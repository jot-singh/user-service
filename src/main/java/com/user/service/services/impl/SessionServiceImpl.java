package com.user.service.services.impl;

import com.user.service.dao.SessionDao;
import com.user.service.entity.Session;
import com.user.service.services.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionDao sessionDao;

    public SessionServiceImpl(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public List<Session> getActiveSessions(String username) {
        return sessionDao.findByUsername(username);
    }

    @Override
    public void terminateSession(String sessionId) {
        sessionDao.deleteById(sessionId);
    }
}
