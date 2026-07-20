package com.finbank.service.impl;

import com.finbank.dto.response.AccountResponse;
import com.finbank.entity.Account;
import com.finbank.entity.AccountStatus;
import com.finbank.entity.AccountType;
import com.finbank.entity.User;
import com.finbank.exception.AccountNotFoundException;
import com.finbank.exception.UnauthorizedAccessException;
import com.finbank.exception.UserNotFoundException;
import com.finbank.repository.AccountRepository;
import com.finbank.repository.UserRepository;
import com.finbank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
    public AccountResponse createAccount(String email, AccountType accountType) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate clean 10-digit account number (Finbank unique style)
        String accountNumber;
        do {
            accountNumber = "10" + String.format("%08d", new Random().nextInt(100000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        Account saved = accountRepository.save(account);
        log.info("Created account: {} of type {} for user {}", saved.getAccountNumber(), accountType, email);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id, String email) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account id " + id + " matches nothing"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Refused: This account is NOT yours");
        }

        return mapToResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts", key = "#email")
    public List<AccountResponse> getMyAccounts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
    public void freezeAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
        log.warn("Account {} frozen by administrative action", account.getAccountNumber());
    }

    @Override
    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
    public void unfreezeAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        log.info("Account {} thawed and reactivated successfully", account.getAccountNumber());
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .build();
    }
}