package com.globe.paymybuddy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globe.paymybuddy.dtos.ConnectionRequestDto;
import com.globe.paymybuddy.dtos.ConnectionResponseDto;
import com.globe.paymybuddy.services.ConnectionService;
import com.globe.paymybuddy.services.JwtFilter;
import com.globe.paymybuddy.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires du ConnectionController.
 * La sécurité (filtres Spring Security) est désactivée via addFilters = false,
 * on ne teste donc pas l'authentification ici : JwtService est simplement mocké
 * pour simuler l'extraction de l'id utilisateur depuis le token.
 */
@WebMvcTest(ConnectionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConnectionService connectionService;

    @MockitoBean
    private JwtService jwtService;

    // Nécessaire uniquement pour que le contexte Spring puisse construire la
    // chaîne de sécurité (springSecurityFilterChain) sans erreur : on mock le
    // filtre lui-même plutôt que sa dépendance UserDetailsServiceImpl.
    @MockitoBean
    private JwtFilter jwtFilter;

    private static final String AUTH_HEADER = "Bearer fake-token";
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        when(jwtService.extractUserId("fake-token")).thenReturn(USER_ID);
    }

    // ---------- POST /api/connections ----------

    @Test
    void createConnection_shouldReturnCreated_whenRequestIsValid() throws Exception {
        ConnectionRequestDto requestDto = new ConnectionRequestDto("friend@example.com");
        ConnectionResponseDto responseDto = new ConnectionResponseDto(10L, "Friend Name", "friend@example.com");

        when(connectionService.addConnection(eq(USER_ID), any(ConnectionRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/connections")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.connectionId").value(10L))
                .andExpect(jsonPath("$.connectedUserName").value("Friend Name"))
                .andExpect(jsonPath("$.connectedUserEmail").value("friend@example.com"));

        verify(connectionService).addConnection(USER_ID, requestDto);
        verify(jwtService).extractUserId("fake-token");
    }

    @Test
    void createConnection_shouldReturnBadRequest_whenBodyIsInvalid() throws Exception {
        // email à null déclenche @NotBlank (et donc le 400)
        ConnectionRequestDto invalidRequest = new ConnectionRequestDto(null);

        mockMvc.perform(post("/api/connections")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(connectionService);
    }

    @Test
    void createConnection_shouldReturnBadRequest_whenEmailFormatIsInvalid() throws Exception {
        ConnectionRequestDto invalidRequest = new ConnectionRequestDto("not-an-email");

        mockMvc.perform(post("/api/connections")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(connectionService);
    }

    // ---------- DELETE /api/connections/{connectionId} ----------

    @Test
    void deleteConnection_shouldReturnNoContent() throws Exception {
        Long connectionId = 5L;
        doNothing().when(connectionService).deleteConnection(USER_ID, connectionId);

        mockMvc.perform(delete("/api/connections/{connectionId}", connectionId)
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isNoContent());

        verify(connectionService).deleteConnection(USER_ID, connectionId);
    }

    // ---------- GET /api/connections ----------

    @Test
    void getAllConnections_shouldReturnListOfConnections() throws Exception {
        ConnectionResponseDto dto1 = new ConnectionResponseDto(1L, "Alice", "alice@example.com");
        ConnectionResponseDto dto2 = new ConnectionResponseDto(2L, "Bob", "bob@example.com");

        when(connectionService.getAllConnections(USER_ID)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/connections")
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].connectionId").value(1L))
                .andExpect(jsonPath("$[1].connectionId").value(2L));

        verify(connectionService).getAllConnections(USER_ID);
    }

    @Test
    void getAllConnections_shouldReturnEmptyList_whenUserHasNoConnections() throws Exception {
        when(connectionService.getAllConnections(USER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/connections")
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}