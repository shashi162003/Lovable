package com.devshashi.lovable.repository;

import com.devshashi.lovable.entity.ChatSession;
import com.devshashi.lovable.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}