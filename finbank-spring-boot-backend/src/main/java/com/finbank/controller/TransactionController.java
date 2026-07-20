package com.finbank.controller;

import com.finbank.dto.request.TransactionRequest;
import com.finbank.dto.response.ApiResponse;
import com.finbank.dto.response.TransactionResponse;
import com.finbank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse res = transactionService.deposit(request);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Deposit action processed successfully!")
                .data(res)
                .build());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse res = transactionService.withdraw(request);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Withdrawal completed")
                .data(res)
                .build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse res = transactionService.transfer(request);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transfer completed successfully")
                .data(res)
                .build());
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getHistory(
            @RequestParam String accountNumber,
            Pageable pageable
    ) {
        Page<TransactionResponse> history = transactionService.getHistory(accountNumber, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<TransactionResponse>>builder()
                .success(true)
                .message("History records processed successfully")
                .data(history)
                .build());
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getByRef(@PathVariable String reference) {
        TransactionResponse res = transactionService.getByReference(reference);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transaction found")
                .data(res)
                .build());
    }
}