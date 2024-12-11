package com.user.service.repository;

import com.user.service.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, String> {
    Session findByUsername(String username);

    Optional<Session> findByToken(String token);

    Optional<Session> findByTokenAndUsername(String token, String username);
}
