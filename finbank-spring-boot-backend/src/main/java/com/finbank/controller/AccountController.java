package com.finbank.controller;

import com.finbank.dto.response.AccountResponse;
import com.finbank.dto.response.ApiResponse;
import com.finbank.entity.AccountType;
import com.finbank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> create(
            @AuthenticationPrincipal UserDetails details,
            @RequestParam AccountType type
    ) {
        AccountResponse response = accountService.createAccount(details.getUsername(), type);
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .success(true)
                .message("Account creation succeeded")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> get(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails details
    ) {
        AccountResponse response = accountService.getAccountById(id, details.getUsername());
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .success(true)
                .message("Account retrieved")
                .data(response)
                .build());
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMy(
            @AuthenticationPrincipal UserDetails details
    ) {
        List<AccountResponse> list = accountService.getMyAccounts(details.getUsername());
        return ResponseEntity.ok(ApiResponse.<List<AccountResponse>>builder()
                .success(true)
                .message("Customer accounts retrieved successfully")
                .data(list)
                .build());
    }
}