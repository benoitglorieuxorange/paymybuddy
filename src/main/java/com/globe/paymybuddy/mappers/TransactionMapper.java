package com.globe.paymybuddy.mappers;


import com.globe.paymybuddy.dtos.TransactionRequestDto;
import com.globe.paymybuddy.dtos.TransactionResponseDto;
import com.globe.paymybuddy.models.Transaction;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    private final UserRepository userRepository;

    public TransactionMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Transaction toEntity(TransactionRequestDto dto, User sender, User receiver) {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        return transaction;
    }

    public TransactionResponseDto toDto(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getReceiver().getEmail(),
                transaction.getAmount(),
                transaction.getDescription()
        );
    }
}
