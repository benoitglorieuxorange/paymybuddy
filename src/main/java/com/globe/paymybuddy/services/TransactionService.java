package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.TransactionRequestDto;
import com.globe.paymybuddy.dtos.TransactionResponseDto;
import com.globe.paymybuddy.exceptions.InsufficientBalanceException;
import com.globe.paymybuddy.mappers.TransactionMapper;
import com.globe.paymybuddy.models.Transaction;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.TransactionRepository;
import com.globe.paymybuddy.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository  userRepository;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.userRepository = userRepository;
    }

    public List<TransactionResponseDto> getAllTransactions(){
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public void deleteTransaction(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    @Transactional // Si une RuntimeException est levée (comme ton InsufficientBalanceException) → rollback automatique (rien n'est écrit)
    public TransactionResponseDto createTransaction(TransactionRequestDto dto, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(dto.receiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer cette transaction");
        }

        sender.setBalance(sender.getBalance().subtract(dto.amount()));
        receiver.setBalance(receiver.getBalance().add(dto.amount()));

        Transaction transaction = transactionMapper.toEntity(dto, sender, receiver);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }


    public List<TransactionResponseDto> getTransactionBySenderId(Long senderId) {
        return transactionRepository.findBySenderId(senderId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }
}
