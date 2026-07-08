package com.globe.paymybuddy.controllers;

import com.globe.paymybuddy.dtos.TransactionRequestDto;
import com.globe.paymybuddy.dtos.TransactionResponseDto;
import com.globe.paymybuddy.services.JwtService;
import com.globe.paymybuddy.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtService jwtService;

    public TransactionController(TransactionService transactionService, JwtService jwtService) {
        this.transactionService = transactionService;
        this.jwtService = jwtService;
    }

//    @GetMapping
//    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions() {
//        List<TransactionResponseDto> transactions = transactionService.getAllTransactions();
//        return ResponseEntity.ok(transactions);
//    }
@GetMapping
public ResponseEntity<List<TransactionResponseDto>> getAllTransactions(
        @RequestHeader("Authorization") String authHeader) {

    String token = authHeader.substring(7); // enlève "Bearer "
    Long userId = jwtService.extractUserId(token);

    List<TransactionResponseDto> transactions = transactionService.getTransactionBySenderId(userId);
    return ResponseEntity.ok(transactions);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("delete transaction id=" + id);
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TransactionRequestDto request) {

        String token = authHeader.substring(7);
        Long senderId = jwtService.extractUserId(token); //recupere l'id du sender dans le token

        TransactionResponseDto response = transactionService.createTransaction(request, senderId);
        return ResponseEntity.ok(response);
    }


}
