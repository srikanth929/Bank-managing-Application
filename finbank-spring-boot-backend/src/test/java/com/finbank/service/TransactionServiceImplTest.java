package com.finbank.service;

import com.finbank.dto.request.TransactionRequest;
import com.finbank.dto.response.TransactionResponse;
import com.finbank.entity.*;
import com.finbank.exception.InsufficientBalanceException;
import com.finbank.repository.AccountRepository;
import com.finbank.repository.TransactionRepository;
import com.finbank.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account senderAccount;
    private Account receiverAccount;
    private TransactionRequest transferRequest;

    @BeforeEach
    void setUp() {
        senderAccount = Account.builder()
                .id(1L)
                .accountNumber("1011111111")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        receiverAccount = Account.builder()
                .id(2L)
                .accountNumber("1022222222")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("500.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        transferRequest = new TransactionRequest();
        transferRequest.setSenderAccount("1011111111");
        transferRequest.setReceiverAccount("1022222222");
        transferRequest.setAmount(new BigDecimal("200.00"));
        transferRequest.setDescription("Payment for rent");
    }

    @Test
    void transfer_Success() {
        when(accountRepository.findByAccountNumber("1011111111")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("1022222222")).thenReturn(Optional.of(receiverAccount));
        
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        TransactionResponse response = transactionService.transfer(transferRequest);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(new BigDecimal("200.00"), response.getAmount());
        assertEquals(new BigDecimal("800.00"), senderAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), receiverAccount.getBalance());

        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(receiverAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        transferRequest.setAmount(new BigDecimal("2000.00")); // Balance is only 1000

        when(accountRepository.findByAccountNumber("1011111111")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("1022222222")).thenReturn(Optional.of(receiverAccount));

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.transfer(transferRequest);
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_FrozenSender_ThrowsException() {
        senderAccount.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findByAccountNumber("1011111111")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("1022222222")).thenReturn(Optional.of(receiverAccount));

        assertThrows(RuntimeException.class, () -> {
            transactionService.transfer(transferRequest);
        });

        verify(accountRepository, never()).save(any(Account.class));
    }
}