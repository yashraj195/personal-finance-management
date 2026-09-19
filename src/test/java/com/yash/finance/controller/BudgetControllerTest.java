package com.yash.finance.controller;

import com.yash.finance.dto.BudgetRequest;
import com.yash.finance.dto.BudgetResponse;
import com.yash.finance.dto.BudgetUtilizationResponse;
import com.yash.finance.dto.MonthlyBudgetOverviewResponse;
import com.yash.finance.entity.Category;
import com.yash.finance.security.JwtAuthenticationFilter;
import com.yash.finance.service.BudgetService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BudgetController.class)
@AutoConfigureMockMvc(addFilters = false)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BudgetService budgetService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                List.of()
        );
    }

    @Test
    void shouldCreateBudgetSuccessfully() throws Exception {

        BudgetResponse response = new BudgetResponse(
                1L,
                Category.FOOD,
                new BigDecimal("10000.00"),
                LocalDate.of(2026, 9, 1)
        );

        when(budgetService.createBudget(
                any(BudgetRequest.class),
                eq("test@example.com")
        )).thenReturn(response);

        String request = """
                {
                    "category": "FOOD",
                    "amount": 10000.00,
                    "budgetMonth": "2026-09-01"
                }
                """;

        mockMvc.perform(
                        post("/api/budgets")
                                .principal(authentication())
                                .contentType("application/json")
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.amount").value(10000.00))
                .andExpect(jsonPath("$.budgetMonth").value("2026-09-01"));

        verify(budgetService).createBudget(
                any(BudgetRequest.class),
                eq("test@example.com")
        );
    }

    @Test
    void shouldReturnBadRequestForInvalidBudget() throws Exception {

        String invalidRequest = """
                {
                    "category": null,
                    "amount": 0,
                    "budgetMonth": null
                }
                """;

        mockMvc.perform(
                        post("/api/budgets")
                                .principal(authentication())
                                .contentType("application/json")
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetBudgetsSuccessfully() throws Exception {

        List<BudgetResponse> response = List.of(
                new BudgetResponse(
                        1L,
                        Category.FOOD,
                        new BigDecimal("10000.00"),
                        LocalDate.of(2026, 9, 1)
                )
        );

        when(budgetService.getBudgets(
                eq("test@example.com")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/budgets")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].category").value("FOOD"))
                .andExpect(jsonPath("$[0].amount").value(10000.00));

        verify(budgetService).getBudgets(
                eq("test@example.com")
        );
    }

    @Test
    void shouldGetBudgetSuccessfully() throws Exception {

        BudgetResponse response = new BudgetResponse(
                1L,
                Category.FOOD,
                new BigDecimal("10000.00"),
                LocalDate.of(2026, 9, 1)
        );

        when(budgetService.getBudget(
                eq(1L),
                eq("test@example.com")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/budgets/1")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.amount").value(10000.00));

        verify(budgetService).getBudget(
                eq(1L),
                eq("test@example.com")
        );
    }

    @Test
    void shouldUpdateBudgetSuccessfully() throws Exception {

        BudgetResponse response = new BudgetResponse(
                1L,
                Category.FOOD,
                new BigDecimal("15000.00"),
                LocalDate.of(2026, 9, 1)
        );

        when(budgetService.updateBudget(
                eq(1L),
                any(BudgetRequest.class),
                eq("test@example.com")
        )).thenReturn(response);

        String request = """
                {
                    "category": "FOOD",
                    "amount": 15000.00,
                    "budgetMonth": "2026-09-01"
                }
                """;

        mockMvc.perform(
                        put("/api/budgets/1")
                                .principal(authentication())
                                .contentType("application/json")
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(15000.00));

        verify(budgetService).updateBudget(
                eq(1L),
                any(BudgetRequest.class),
                eq("test@example.com")
        );
    }

    @Test
    void shouldDeleteBudgetSuccessfully() throws Exception {

        mockMvc.perform(
                        delete("/api/budgets/1")
                                .principal(authentication())
                )
                .andExpect(status().isNoContent());

        verify(budgetService).deleteBudget(
                eq(1L),
                eq("test@example.com")
        );
    }

    @Test
    void shouldGetBudgetUtilizationSuccessfully() throws Exception {

        BudgetUtilizationResponse response =
                new BudgetUtilizationResponse(
                        1L,
                        Category.FOOD,
                        new BigDecimal("10000.00"),
                        new BigDecimal("5000.00"),
                        new BigDecimal("5000.00"),
                        new BigDecimal("50.00"),
                        "WITHIN_LIMIT"
                );

        when(budgetService.getBudgetUtilization(
                eq(1L),
                eq("test@example.com")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/budgets/1/utilization")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budgetId").value(1))
                .andExpect(jsonPath("$.budgetAmount").value(10000.00))
                .andExpect(jsonPath("$.spentAmount").value(5000.00))
                .andExpect(jsonPath("$.remainingAmount").value(5000.00))
                .andExpect(jsonPath("$.utilizationPercentage").value(50.00))
                .andExpect(jsonPath("$.status").value("WITHIN_LIMIT"));

        verify(budgetService).getBudgetUtilization(
                eq(1L),
                eq("test@example.com")
        );
    }

    @Test
    void shouldGetMonthlyOverviewSuccessfully() throws Exception {

        MonthlyBudgetOverviewResponse response =
                new MonthlyBudgetOverviewResponse(
                        LocalDate.of(2026, 9, 1),
                        new BigDecimal("30000.00"),
                        new BigDecimal("15000.00"),
                        new BigDecimal("15000.00"),
                        new BigDecimal("50.00"),
                        List.of()
                );

        when(budgetService.getMonthlyOverview(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/budgets/overview")
                                .param("month", "2026-09-01")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2026-09-01"))
                .andExpect(jsonPath("$.totalBudget").value(30000.00))
                .andExpect(jsonPath("$.totalSpent").value(15000.00))
                .andExpect(jsonPath("$.totalRemaining").value(15000.00))
                .andExpect(jsonPath("$.overallUtilizationPercentage").value(50.00));

        verify(budgetService).getMonthlyOverview(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        );
    }
}