package com.net.messageService.scheduler;

import com.net.messageService.model.SendMessage;
import com.net.messageService.repository.SendMsgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

@Component
public class SmsProcessingScheduler {
    @Autowired
    private SendMsgRepository repository;
    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(fixedDelay = 1000)
    public void processNewMessages() {
        List<SendMessage> newMsgs = repository.findByStatus("NEW");
        for (SendMessage msg : newMsgs) {
            try {

                msg.setStatus("INPROCESS");
                repository.save(msg);

                String url = String.format("http://localhost:8080/telco/smsc?account_id=%d&mobile=%s&message=%s",
                        msg.getUser().getAccountId(), msg.getMobile(), URLEncoder.encode(msg.getMessage(), StandardCharsets.UTF_8));

                String response = restTemplate.getForObject(url, String.class);


                msg.setTelcoResponse(response);
                msg.setStatus("SENT");
                msg.setSentTs(new Timestamp(System.currentTimeMillis()));
                repository.save(msg);

            } catch (Exception ex) {
                msg.setTelcoResponse("Error: " + ex.getMessage());
                msg.setStatus("FAILED");
                repository.save(msg);
            }
        }
    }
}
