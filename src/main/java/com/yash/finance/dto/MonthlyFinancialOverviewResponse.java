package com.yash.finance.dto;

import com.yash.finance.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlyFinancialOverviewResponse(
        LocalDate month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance,
        BigDecimal totalBudget,
        BigDecimal totalBudgetSpent,
        BigDecimal totalBudgetRemaining,
        BigDecimal budgetUtilizationPercentage,
        Category highestSpendingCategory,
        BigDecimal highestSpendingAmount
) {
}