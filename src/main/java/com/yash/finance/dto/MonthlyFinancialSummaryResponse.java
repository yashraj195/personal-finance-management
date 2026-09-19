package com.yash.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlyFinancialSummaryResponse(
        LocalDate month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}