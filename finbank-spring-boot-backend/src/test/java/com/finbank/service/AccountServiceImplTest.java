package com.finbank.service;

import com.finbank.dto.response.AccountResponse;
import com.finbank.entity.*;
import com.finbank.repository.AccountRepository;
import com.finbank.repository.UserRepository;
import com.finbank.service.impl.AccountServiceImpl;
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
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(10L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .enabled(true)
                .build();
    }

    @Test
    void createAccount_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(50L);
            return account;
        });

        AccountResponse response = accountService.createAccount("john.doe@example.com", AccountType.SAVINGS);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(AccountType.SAVINGS.name(), response.getAccountType());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}