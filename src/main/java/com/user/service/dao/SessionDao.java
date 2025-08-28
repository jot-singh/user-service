package com.user.service.dao;

import com.user.service.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionDao extends JpaRepository<Session, String> {
    List<Session> findByUsername(String username);
    Optional<Session> findByTokenAndUsername(String token, String username);
}

