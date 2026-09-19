package com.yash.finance.controller;

import com.yash.finance.dto.TransactionRequest;
import com.yash.finance.dto.TransactionResponse;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.security.JwtAuthenticationFilter;
import com.yash.finance.service.TransactionService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturnBadRequestForInvalidTransaction() throws Exception {

        String invalidRequest = """
                {
                    "type": null,
                    "category": null,
                    "amount": 0,
                    "transactionDate": null,
                    "description": "Invalid transaction"
                }
                """;

        mockMvc.perform(
                        post("/api/transactions")
                                .contentType("application/json")
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {

        TransactionResponse response = new TransactionResponse(
                1L,
                TransactionType.EXPENSE,
                Category.FOOD,
                new BigDecimal("500.00"),
                LocalDate.of(2026, 9, 19),
                "Dinner"
        );

        when(transactionService.createTransaction(
                any(TransactionRequest.class),
                eq("test@example.com")
        )).thenReturn(response);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "test@example.com",
                        null,
                        List.of()
                );

        String validRequest = """
            {
                "type": "EXPENSE",
                "category": "FOOD",
                "amount": 500.00,
                "transactionDate": "2026-09-19",
                "description": "Dinner"
            }
            """;

        mockMvc.perform(
                        post("/api/transactions")
                                .principal(authentication)
                                .contentType("application/json")
                                .content(validRequest)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.description").value("Dinner"));

        verify(transactionService).createTransaction(
                any(TransactionRequest.class),
                eq("test@example.com")
        );
    }
}