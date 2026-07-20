package com.finbank.service.impl;

import com.finbank.dto.request.BeneficiaryRequest;
import com.finbank.entity.Beneficiary;
import com.finbank.entity.User;
import com.finbank.exception.UnauthorizedAccessException;
import com.finbank.exception.UserNotFoundException;
import com.finbank.repository.BeneficiaryRepository;
import com.finbank.repository.UserRepository;
import com.finbank.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;

    @Override
    public void addBeneficiary(String email, BeneficiaryRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Beneficiary beneficiary = Beneficiary.builder()
                .beneficiaryName(request.getBeneficiaryName())
                .accountNumber(request.getAccountNumber())
                .bankName(request.getBankName())
                .user(user)
                .build();

        beneficiaryRepository.save(beneficiary);
    }

    @Override
    public List<Beneficiary> getMyBeneficiaries(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return beneficiaryRepository.findByUserId(user.getId());
    }

    @Override
    public void deleteBeneficiary(Long id, String email) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiary matches nothing"));

        if (!beneficiary.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Forbidden: This beneficiary matches another client");
        }
        beneficiaryRepository.delete(beneficiary);
    }
}