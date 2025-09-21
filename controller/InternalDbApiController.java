package com.net.messageService.controller;
import com.net.messageService.model.SendMessage;
import com.net.messageService.repository.SendMsgRepository;
import com.net.messageService.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;


@RestController
@RequestMapping("/internal")
public class InternalDbApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SendMsgRepository msgRepository;

    @GetMapping("/getAccount")
    public ResponseEntity<Integer> getAccountId(@RequestParam String username, @RequestParam String password) {
        return userRepository.findByUsernameAndPassword(username, password)
                .map(u -> ResponseEntity.ok(u.getAccountId()))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @PostMapping("/insertSendMsg")
    public ResponseEntity<String> insertSendMsg(@Valid @RequestBody SendMessage message) {
        message.setReceivedTs(new Timestamp(System.currentTimeMillis()));
        message.setStatus("NEW");
        msgRepository.save(message);
System.out.println("message send : ");
        return ResponseEntity.ok().build();
    }
    }
