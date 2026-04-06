package com.devshashi.distributed_lovable.intelligence_service.repository;

import com.devshashi.distributed_lovable.intelligence_service.entity.ChatMessage;
import com.devshashi.distributed_lovable.intelligence_service.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        SELECT DISTINCT m FROM ChatMessage m
        LEFT JOIN FETCH m.events e
        WHERE m.chatSession = :chatSession
        ORDER BY m.createdAt ASC, e.sequenceOrder ASC
    """)
    List<ChatMessage> findByChatSession(@Param("chatSession") ChatSession chatSession);

}