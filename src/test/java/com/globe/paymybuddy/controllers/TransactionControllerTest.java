package com.globe.paymybuddy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globe.paymybuddy.dtos.TransactionRequestDto;
import com.globe.paymybuddy.dtos.TransactionResponseDto;
import com.globe.paymybuddy.services.JwtFilter;
import com.globe.paymybuddy.services.JwtService;
import com.globe.paymybuddy.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires du TransactionController.
 * La sécurité (filtres Spring Security) est désactivée via addFilters = false,
 * on ne teste donc pas l'authentification ici : JwtService est mocké pour
 * simuler l'extraction de l'id utilisateur depuis le token. JwtFilter est
 * également mocké uniquement pour permettre au contexte Spring de construire
 * la chaîne de sécurité sans erreur (voir UserDetailsServiceImpl manquant).
 */
@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private static final String AUTH_HEADER = "Bearer fake-token";
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        when(jwtService.extractUserId("fake-token")).thenReturn(USER_ID);
    }

    // ---------- GET /api/transactions ----------

    @Test
    void getAllTransactions_shouldReturnListOfTransactions() throws Exception {
        TransactionResponseDto dto1 = new TransactionResponseDto(1, "alice@example.com", BigDecimal.valueOf(50), "Repas");
        TransactionResponseDto dto2 = new TransactionResponseDto(2, "bob@example.com", BigDecimal.valueOf(20), "Café");

        when(transactionService.getTransactionBySenderId(USER_ID)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].receiverEmail").value("alice@example.com"))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(transactionService).getTransactionBySenderId(USER_ID);
        verify(jwtService).extractUserId("fake-token");
    }

    @Test
    void getAllTransactions_shouldReturnEmptyList_whenUserHasNoTransactions() throws Exception {
        when(transactionService.getTransactionBySenderId(USER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ---------- DELETE /api/transactions/{id} ----------

    @Test
    void deleteTransaction_shouldReturnOkWithConfirmationMessage() throws Exception {
        Long transactionId = 5L;
        doNothing().when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(content().string("delete transaction id=" + transactionId));

        verify(transactionService).deleteTransaction(transactionId);
    }

    // ---------- POST /api/transactions ----------

    @Test
    void createTransaction_shouldReturnOk_whenRequestIsValid() throws Exception {
        TransactionRequestDto requestDto = new TransactionRequestDto(
                "receiver@example.com", BigDecimal.valueOf(100), "Remboursement");
        TransactionResponseDto responseDto = new TransactionResponseDto(
                10, "receiver@example.com", BigDecimal.valueOf(100), "Remboursement");

        when(transactionService.createTransaction(eq(requestDto), eq(USER_ID)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.receiverEmail").value("receiver@example.com"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.description").value("Remboursement"));

        verify(transactionService).createTransaction(requestDto, USER_ID);
        verify(jwtService).extractUserId("fake-token");
    }
}