package com.finbank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotBlank(message = "Source account number is required")
    private String senderAccount;

    private String receiverAccount;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be strictly positive")
    private BigDecimal amount;

    @Size(max = 255, message = "Description must be within 255 characters")
    private String description;
}