package com.devshashi.distributed_lovable.intelligence_service.repository;

import com.devshashi.distributed_lovable.intelligence_service.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {
}