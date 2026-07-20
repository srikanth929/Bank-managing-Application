package com.finbank.controller;

import com.finbank.dto.response.ApiResponse;
import com.finbank.entity.User;
import com.finbank.repository.UserRepository;
import com.finbank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AccountService accountService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.<List<User>>builder()
                .success(true)
                .message("All users loaded successfully")
                .data(userRepository.findAll())
                .build());
    }

    @PutMapping("/freeze-account/{id}")
    public ResponseEntity<ApiResponse<String>> freeze(@PathVariable Long id) {
        accountService.freezeAccount(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Account frozen successfully")
                .build());
    }

    @PutMapping("/unfreeze-account/{id}")
    public ResponseEntity<ApiResponse<String>> unfreeze(@PathVariable Long id) {
        accountService.unfreezeAccount(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Account un-frozen successfully")
                .build());
    }
}