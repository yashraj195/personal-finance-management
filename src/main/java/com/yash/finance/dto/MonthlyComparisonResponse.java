package com.yash.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlyComparisonResponse(
        LocalDate currentMonth,
        LocalDate previousMonth,
        BigDecimal currentIncome,
        BigDecimal previousIncome,
        BigDecimal currentExpense,
        BigDecimal previousExpense,
        BigDecimal incomeChange,
        BigDecimal expenseChange,
        BigDecimal currentNetBalance,
        BigDecimal previousNetBalance,
        BigDecimal netBalanceChange
) {
}