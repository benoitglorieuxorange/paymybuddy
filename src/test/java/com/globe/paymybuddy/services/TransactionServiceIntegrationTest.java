package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.TransactionRequestDto;
import com.globe.paymybuddy.exceptions.InsufficientBalanceException;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setEmail("sender@test.com");
        sender.setUsername("sender");
        sender.setPassword("encoded-password");
        sender.setBalance(new BigDecimal("100.00"));
        sender = userRepository.save(sender);

        receiver = new User();
        receiver.setEmail("receiver@test.com");
        receiver.setUsername("receiver");
        receiver.setPassword("encoded-password");
        receiver.setBalance(new BigDecimal("50.00"));
        receiver = userRepository.save(receiver);
    }

    @Test
    void createTransaction_shouldRollback_whenInsufficientBalance() {
        BigDecimal senderInitialBalance = sender.getBalance();
        BigDecimal receiverInitialBalance = receiver.getBalance();

        TransactionRequestDto request = new TransactionRequestDto(
                receiver.getEmail(),
                new BigDecimal("999999"),
                "test"
        );

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.createTransaction(request, sender.getId())
        );

        User senderAfter = userRepository.findById(sender.getId()).orElseThrow();
        User receiverAfter = userRepository.findById(receiver.getId()).orElseThrow();

        assertEquals(senderInitialBalance, senderAfter.getBalance());
        assertEquals(receiverInitialBalance, receiverAfter.getBalance());
    }

    @Test
    void createTransaction_shouldCommit_whenBalanceSufficient() {
        BigDecimal senderInitial = sender.getBalance();
        BigDecimal receiverInitial = receiver.getBalance();
        BigDecimal amount = new BigDecimal("10.00");

        TransactionRequestDto request = new TransactionRequestDto(
                receiver.getEmail(),
                amount,
                "test ok"
        );

        transactionService.createTransaction(request, sender.getId());

        User senderAfter = userRepository.findById(sender.getId()).orElseThrow();
        User receiverAfter = userRepository.findById(receiver.getId()).orElseThrow();

        assertEquals(senderInitial.subtract(amount), senderAfter.getBalance());
        assertEquals(receiverInitial.add(amount), receiverAfter.getBalance());
    }
}