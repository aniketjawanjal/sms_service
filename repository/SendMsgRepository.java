package com.net.messageService.repository;

import com.net.messageService.model.SendMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SendMsgRepository extends JpaRepository<SendMessage, Integer> {
    List<SendMessage> findByStatus(String status);
}
