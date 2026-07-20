package com.finbank.service;

import com.finbank.dto.response.AccountResponse;
import com.finbank.entity.AccountType;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(String email, AccountType accountType);
    AccountResponse getAccountById(Long id, String email);
    List<AccountResponse> getMyAccounts(String email);
    void freezeAccount(Long id);
    void unfreezeAccount(Long id);
}