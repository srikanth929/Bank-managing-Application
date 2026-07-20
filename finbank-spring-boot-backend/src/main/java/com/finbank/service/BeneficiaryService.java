package com.finbank.service;

import com.finbank.dto.request.BeneficiaryRequest;
import com.finbank.entity.Beneficiary;
import java.util.List;

public interface BeneficiaryService {
    void addBeneficiary(String email, BeneficiaryRequest request);
    List<Beneficiary> getMyBeneficiaries(String email);
    void deleteBeneficiary(Long id, String email);
}