package com.finbank.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private String transactionReference;
    private String transactionType;
    private BigDecimal amount;
    private String senderAccount;
    private String receiverAccount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}