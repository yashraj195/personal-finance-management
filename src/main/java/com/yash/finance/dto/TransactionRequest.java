package com.yash.finance.dto;

import com.yash.finance.entity.Category;
import com.yash.finance.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(

        @NotNull
        TransactionType type,

        @NotNull
        Category category,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        LocalDate transactionDate,

        @Size(max = 500)
        String description
) {
}