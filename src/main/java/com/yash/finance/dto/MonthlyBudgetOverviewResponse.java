package com.yash.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MonthlyBudgetOverviewResponse(
        LocalDate month,
        BigDecimal totalBudget,
        BigDecimal totalSpent,
        BigDecimal totalRemaining,
        BigDecimal overallUtilizationPercentage,
        List<BudgetUtilizationResponse> budgets
) {
}