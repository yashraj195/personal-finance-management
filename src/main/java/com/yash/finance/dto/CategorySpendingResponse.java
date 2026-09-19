package com.yash.finance.dto;

import com.yash.finance.entity.Category;

import java.math.BigDecimal;

public record CategorySpendingResponse(
        Category category,
        BigDecimal amount,
        BigDecimal percentage
) {
}