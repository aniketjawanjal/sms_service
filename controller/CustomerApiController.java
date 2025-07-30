package com.net.messageService.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/telco")
public class CustomerApiController {


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/sendmsg")
    public ResponseEntity<String> sendMessage(@RequestParam String username, @RequestParam String password,
                                              @RequestParam String mobile, @RequestParam String message) {

        if (mobile == null || mobile.length() != 10 || !mobile.matches("\\d{10}") || message == null || message.isEmpty() || message.length() > 160) {
            return ResponseEntity.badRequest().body("STATUS: REJECTED~~RESPONSE_CODE: FAILURE~~400");
        }

        String url = "http://localhost:8080/internal/getAccount?username=" + username + "&password=" + password;
        ResponseEntity<Integer> accountResponse = restTemplate.getForEntity(url, Integer.class);

        if (!accountResponse.getStatusCode().is2xxSuccessful() || accountResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("STATUS: REJECTED~~RESPONSE_CODE: FAILURE~~401");
        }

        String ackId = UUID.randomUUID().toString();
        int accountId = accountResponse.getBody();

        JSONObject json = new JSONObject();
        json.put("ack_id", ackId);
        json.put("account_id", accountId);
        json.put("mobile", mobile);
        json.put("message", message);

        kafkaTemplate.send("sms-topic", json.toString());

        return ResponseEntity.ok("STATUS: ACCEPTED~~RESPONSE_CODE: SUCCESS~~" + ackId);
    }
}


