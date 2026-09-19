package com.yash.finance.dto;

import com.yash.finance.entity.Category;

import java.math.BigDecimal;

public record BudgetUtilizationResponse(
        Long budgetId,
        Category category,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal utilizationPercentage,
        String status
) {
}