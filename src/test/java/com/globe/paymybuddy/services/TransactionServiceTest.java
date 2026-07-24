package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.TransactionRequestDto;
import com.globe.paymybuddy.dtos.TransactionResponseDto;
import com.globe.paymybuddy.exceptions.InsufficientBalanceException;
import com.globe.paymybuddy.mappers.TransactionMapper;
import com.globe.paymybuddy.models.Transaction;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.TransactionRepository;
import com.globe.paymybuddy.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void deleteTransactionShouldCallRepositoryDeleteById() {
        Long transactionId = 5L;

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository).deleteById(transactionId);
    }






    @Test
    void createTransactionShouldThrowWhenSenderNotFound() {
        Long senderId = 999L;
        TransactionRequestDto request = new TransactionRequestDto("receiver@mail.com", new BigDecimal("25.00"), "Gift");

        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.createTransaction(request, senderId));

        assertEquals("Sender not found", exception.getMessage());
        verify(userRepository).findById(senderId);
        verify(userRepository, never()).findByEmail(request.receiverEmail());
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void createTransactionShouldThrowWhenReceiverNotFound() {
        Long senderId = 1L;
        TransactionRequestDto request = new TransactionRequestDto("unknown@mail.com", new BigDecimal("25.00"), "Gift");
        User sender = userWithIdAndEmail(senderId, "sender@mail.com");

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail(request.receiverEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.createTransaction(request, senderId));

        assertEquals("Receiver not found", exception.getMessage());
        verify(userRepository).findById(senderId);
        verify(userRepository).findByEmail(request.receiverEmail());
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void getTransactionBySenderIdShouldReturnMappedDtos() {
        Long senderId = 1L;
        Transaction tx1 = new Transaction();
        Transaction tx2 = new Transaction();
        TransactionResponseDto dto1 = new TransactionResponseDto(1, "a@mail.com", new BigDecimal("8.00"), "Coffee");
        TransactionResponseDto dto2 = new TransactionResponseDto(2, "b@mail.com", new BigDecimal("12.50"), "Snacks");

        when(transactionRepository.findBySenderId(senderId)).thenReturn(List.of(tx1, tx2));
        when(transactionMapper.toDto(tx1)).thenReturn(dto1);
        when(transactionMapper.toDto(tx2)).thenReturn(dto2);

        List<TransactionResponseDto> result = transactionService.getTransactionBySenderId(senderId);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(transactionRepository).findBySenderId(senderId);
        verify(transactionMapper).toDto(tx1);
        verify(transactionMapper).toDto(tx2);
    }






    private User userWithIdAndEmail(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}

