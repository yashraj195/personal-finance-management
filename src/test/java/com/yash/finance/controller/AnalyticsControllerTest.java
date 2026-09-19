package com.yash.finance.controller;

import com.yash.finance.dto.CategorySpendingResponse;
import com.yash.finance.dto.HighestSpendingCategoryResponse;
import com.yash.finance.dto.MonthlyComparisonResponse;
import com.yash.finance.dto.MonthlyFinancialOverviewResponse;
import com.yash.finance.dto.MonthlyFinancialSummaryResponse;
import com.yash.finance.entity.Category;
import com.yash.finance.security.JwtAuthenticationFilter;
import com.yash.finance.service.AnalyticsService;

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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

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
    void shouldGetMonthlySummarySuccessfully() throws Exception {

        MonthlyFinancialSummaryResponse response =
                new MonthlyFinancialSummaryResponse(
                        LocalDate.of(2026, 9, 1),
                        new BigDecimal("50000.00"),
                        new BigDecimal("30000.00"),
                        new BigDecimal("20000.00")
                );

        when(analyticsService.getMonthlySummary(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/analytics/monthly-summary")
                                .param("month", "2026-09-01")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2026-09-01"))
                .andExpect(jsonPath("$.totalIncome").value(50000.00))
                .andExpect(jsonPath("$.totalExpense").value(30000.00))
                .andExpect(jsonPath("$.netBalance").value(20000.00));

        verify(analyticsService).getMonthlySummary(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        );
    }

    @Test
    void shouldGetCategorySpendingSuccessfully() throws Exception {

        List<CategorySpendingResponse> response = List.of(
                new CategorySpendingResponse(
                        Category.FOOD,
                        new BigDecimal("10000.00"),
                        new BigDecimal("50.00")
                )
        );

        when(analyticsService.getCategorySpending(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/analytics/category-spending")
                                .param("month", "2026-09-01")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("FOOD"))
                .andExpect(jsonPath("$[0].amount").value(10000.00))
                .andExpect(jsonPath("$[0].percentage").value(50.00));

        verify(analyticsService).getCategorySpending(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        );
    }

    @Test
    void shouldGetHighestSpendingCategorySuccessfully() throws Exception {

        HighestSpendingCategoryResponse response =
                new HighestSpendingCategoryResponse(
                        Category.FOOD,
                        new BigDecimal("10000.00"),
                        new BigDecimal("50.00")
                );

        when(analyticsService.getHighestSpendingCategory(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/analytics/highest-spending")
                                .param("month", "2026-09-01")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.amount").value(10000.00))
                .andExpect(jsonPath("$.percentage").value(50.00));

        verify(analyticsService).getHighestSpendingCategory(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        );
    }

    @Test
    void shouldGetMonthlyOverviewSuccessfully() throws Exception {

        MonthlyFinancialOverviewResponse response =
                new MonthlyFinancialOverviewResponse(
                        LocalDate.of(2026, 9, 1),
                        new BigDecimal("50000.00"),
                        new BigDecimal("30000.00"),
                        new BigDecimal("20000.00"),
                        new BigDecimal("40000.00"),
                        new BigDecimal("25000.00"),
                        new BigDecimal("15000.00"),
                        new BigDecimal("62.50"),
                        Category.FOOD,
                        new BigDecimal("10000.00")
                );

        when(analyticsService.getMonthlyOverview(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/analytics/overview")
                                .param("month", "2026-09-01")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2026-09-01"))
                .andExpect(jsonPath("$.totalIncome").value(50000.00))
                .andExpect(jsonPath("$.totalExpense").value(30000.00))
                .andExpect(jsonPath("$.netBalance").value(20000.00))
                .andExpect(jsonPath("$.totalBudget").value(40000.00))
                .andExpect(jsonPath("$.totalBudgetSpent").value(25000.00))
                .andExpect(jsonPath("$.totalBudgetRemaining").value(15000.00))
                .andExpect(jsonPath("$.budgetUtilizationPercentage").value(62.50))
                .andExpect(jsonPath("$.highestSpendingCategory").value("FOOD"))
                .andExpect(jsonPath("$.highestSpendingAmount").value(10000.00));

        verify(analyticsService).getMonthlyOverview(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        );
    }

    @Test
    void shouldGetMonthlyComparisonSuccessfully() throws Exception {

        MonthlyComparisonResponse response =
                new MonthlyComparisonResponse(
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 8, 1),
                        new BigDecimal("50000.00"),
                        new BigDecimal("40000.00"),
                        new BigDecimal("30000.00"),
                        new BigDecimal("25000.00"),
                        new BigDecimal("25.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("20000.00"),
                        new BigDecimal("15000.00"),
                        new BigDecimal("33.33")
                );

        when(analyticsService.getMonthlyComparison(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/analytics/monthly-comparison")
                                .param("month", "2026-09-01")
                                .principal(authentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentMonth").value("2026-09-01"))
                .andExpect(jsonPath("$.previousMonth").value("2026-08-01"))
                .andExpect(jsonPath("$.currentIncome").value(50000.00))
                .andExpect(jsonPath("$.previousIncome").value(40000.00))
                .andExpect(jsonPath("$.currentExpense").value(30000.00))
                .andExpect(jsonPath("$.previousExpense").value(25000.00))
                .andExpect(jsonPath("$.incomeChange").value(25.00))
                .andExpect(jsonPath("$.expenseChange").value(20.00))
                .andExpect(jsonPath("$.currentNetBalance").value(20000.00))
                .andExpect(jsonPath("$.previousNetBalance").value(15000.00))
                .andExpect(jsonPath("$.netBalanceChange").value(33.33));

        verify(analyticsService).getMonthlyComparison(
                eq("test@example.com"),
                eq(LocalDate.of(2026, 9, 1))
        );
    }
}