package com.user.service.controller;

import com.user.service.dto.response.BaseResponseDto;
import com.user.service.services.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<List<?>> getUserSessions(@PathVariable String username) {
        return ResponseEntity.ok(sessionService.getActiveSessions(username));
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasPermission(#sessionId, 'session', 'DELETE') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto> terminateSession(@PathVariable String sessionId) {
        sessionService.terminateSession(sessionId);
        return ResponseEntity.ok(BaseResponseDto.builder().message("Session terminated successfully").build());
    }
}
