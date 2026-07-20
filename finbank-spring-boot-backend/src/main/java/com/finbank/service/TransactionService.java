package com.finbank.service;

import com.finbank.dto.request.TransactionRequest;
import com.finbank.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionResponse deposit(TransactionRequest request);
    TransactionResponse withdraw(TransactionRequest request);
    TransactionResponse transfer(TransactionRequest request);
    Page<TransactionResponse> getHistory(String accountNumber, Pageable pageable);
    TransactionResponse getByReference(String reference);
}