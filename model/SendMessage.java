package com.net.messageService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "send_msg")
@Data
public class SendMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String mobile;
    @NotBlank
    @Column(length = 160)
    private String message;
    @NotBlank
    private String status;
    private Timestamp receivedTs;

    private Timestamp sentTs;
    private String telcoResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private User user;
}
