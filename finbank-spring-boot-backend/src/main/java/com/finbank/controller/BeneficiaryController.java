package com.finbank.controller;

import com.finbank.dto.request.BeneficiaryRequest;
import com.finbank.dto.response.ApiResponse;
import com.finbank.entity.Beneficiary;
import com.finbank.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> add(
            @AuthenticationPrincipal UserDetails details,
            @Valid @RequestBody BeneficiaryRequest request
    ) {
        beneficiaryService.addBeneficiary(details.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Beneficiary successfully recorded")
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Beneficiary>>> getMy(
            @AuthenticationPrincipal UserDetails details
    ) {
        List<Beneficiary> list = beneficiaryService.getMyBeneficiaries(details.getUsername());
        return ResponseEntity.ok(ApiResponse.<List<Beneficiary>>builder()
                .success(true)
                .message("Beneficiaries fetched")
                .data(list)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails details
    ) {
        beneficiaryService.deleteBeneficiary(id, details.getUsername());
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Beneficiary removed successfully")
                .build());
    }
}