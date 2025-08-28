package com.user.service.services;

import com.user.service.entity.Session;
import java.util.List;

public interface SessionService {
    List<Session> getActiveSessions(String username);
    void terminateSession(String sessionId);
}
