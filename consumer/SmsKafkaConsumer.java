package com.net.messageService.consumer;

import com.net.messageService.model.SendMessage;
import com.net.messageService.model.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.client.RestTemplate;

public class SmsKafkaConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "sms-topic", groupId = "sms-group")
    public void listen(String message) {
        try {
            JSONObject json = new JSONObject(message);
            SendMessage sendMessage = new SendMessage();
            User user = new User();
            user.setAccountId(json.getInt("account_id"));
            sendMessage.setUser(user);
            sendMessage.setMobile(json.getString("mobile"));
            sendMessage.setMessage(json.getString("message"));

            HttpEntity<SendMessage> req = new HttpEntity<>(sendMessage);
            restTemplate.postForEntity("http://localhost:8080/internal/insertSendMsg", req, String.class);

        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
        }
    }

}
