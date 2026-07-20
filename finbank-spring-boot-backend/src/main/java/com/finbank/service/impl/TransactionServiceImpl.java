package com.finbank.service.impl;

import com.finbank.dto.request.TransactionRequest;
import com.finbank.dto.response.TransactionResponse;
import com.finbank.entity.*;
import com.finbank.exception.AccountNotFoundException;
import com.finbank.exception.InsufficientBalanceException;
import com.finbank.repository.AccountRepository;
import com.finbank.repository.TransactionRepository;
import com.finbank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getSenderAccount())
                .orElseThrow(() -> new AccountNotFoundException("Deposit destination account matches nothing"));

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new RuntimeException("Acccount is frozen! Deposit suspended.");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction trx = Transaction.builder()
                .transactionReference("TXD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .senderAccount("External-Deposit")
                .receiverAccount(account.getAccountNumber())
                .description(request.getDescription())
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction saved = transactionRepository.save(trx);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getSenderAccount())
                .orElseThrow(() -> new AccountNotFoundException("Withdraw source account matches nothing"));

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new RuntimeException("Refused: Account is frozen!");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient transaction scope: requested " + request.getAmount() + " but balance is " + account.getBalance());
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction trx = Transaction.builder()
                .transactionReference("TXW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .senderAccount(account.getAccountNumber())
                .receiverAccount("Cash-Dispensation")
                .description(request.getDescription())
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction saved = transactionRepository.save(trx);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransactionRequest request) {
        Account sender = accountRepository.findByAccountNumber(request.getSenderAccount())
                .orElseThrow(() -> new AccountNotFoundException("Sender account matches nothing"));

        Account receiver = accountRepository.findByAccountNumber(request.getReceiverAccount())
                .orElseThrow(() -> new AccountNotFoundException("Recipient account matches nothing"));

        if (sender.getStatus() == AccountStatus.FROZEN) {
            throw new RuntimeException("Refused: Sender account is currently frozen!");
        }
        if (receiver.getStatus() == AccountStatus.FROZEN) {
            throw new RuntimeException("Refused: Recipient account is currently frozen!");
        }

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient funds inside source account: requested " + request.getAmount() + " but balance is " + sender.getBalance());
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction trx = Transaction.builder()
                .transactionReference("TXT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .transactionType(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .senderAccount(sender.getAccountNumber())
                .receiverAccount(receiver.getAccountNumber())
                .description(request.getDescription())
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction saved = transactionRepository.save(trx);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getHistory(String accountNumber, Pageable pageable) {
        if (!accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountNotFoundException("Account does not exist");
        }
        return transactionRepository.findByAccountNumber(accountNumber, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getByReference(String reference) {
        return transactionRepository.findByTransactionReference(reference)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Transaction ref " + reference + " matches nothing"));
    }

    private TransactionResponse mapToResponse(Transaction trx) {
        return TransactionResponse.builder()
                .transactionReference(trx.getTransactionReference())
                .transactionType(trx.getTransactionType().name())
                .amount(trx.getAmount())
                .senderAccount(trx.getSenderAccount())
                .receiverAccount(trx.getReceiverAccount())
                .description(trx.getDescription())
                .status(trx.getStatus().name())
                .createdAt(trx.getCreatedAt())
                .build();
    }
}