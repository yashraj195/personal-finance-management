package com.yash.finance.dto;

import com.yash.finance.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetResponse(
        Long id,
        Category category,
        BigDecimal amount,
        LocalDate budgetMonth
) {
}