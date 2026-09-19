package com.yash.finance.dto;

import com.yash.finance.entity.Category;
import com.yash.finance.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        TransactionType type,
        Category category,
        BigDecimal amount,
        LocalDate transactionDate,
        String description
) {
}