package com.yash.finance.service;

import com.yash.finance.exception.ResourceNotFoundException;
import com.yash.finance.dto.MonthlyComparisonResponse;
import com.yash.finance.service.BudgetService;
import com.yash.finance.repository.BudgetRepository;
import com.yash.finance.dto.CategorySpendingResponse;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.repository.CategorySpendingProjection;
import com.yash.finance.dto.HighestSpendingCategoryResponse;
import com.yash.finance.dto.BudgetUtilizationResponse;
import com.yash.finance.dto.MonthlyFinancialOverviewResponse;
import com.yash.finance.dto.MonthlyFinancialSummaryResponse;
import com.yash.finance.entity.Budget;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import com.yash.finance.repository.AnalyticsRepository;
import com.yash.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetService budgetService;

    public AnalyticsService(
            AnalyticsRepository analyticsRepository,
            UserRepository userRepository,
            BudgetRepository budgetRepository,
            BudgetService budgetService) {

        this.analyticsRepository = analyticsRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.budgetService = budgetService;
    }

    public MonthlyFinancialSummaryResponse getMonthlySummary(
            String email,
            LocalDate month) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        LocalDate startDate = month.withDayOfMonth(1);

        LocalDate endDate =
                month.withDayOfMonth(month.lengthOfMonth());

        BigDecimal totalIncome =
                analyticsRepository.sumByTypeAndDateRange(
                        user,
                        TransactionType.INCOME,
                        startDate,
                        endDate
                );

        BigDecimal totalExpense =
                analyticsRepository.sumByTypeAndDateRange(
                        user,
                        TransactionType.EXPENSE,
                        startDate,
                        endDate
                );

        BigDecimal netBalance =
                totalIncome.subtract(totalExpense);

        return new MonthlyFinancialSummaryResponse(
                month,
                totalIncome,
                totalExpense,
                netBalance
        );
    }

    public List<CategorySpendingResponse> getCategorySpending(
            String email,
            LocalDate month) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        LocalDate startDate = month.withDayOfMonth(1);

        LocalDate endDate =
                month.withDayOfMonth(month.lengthOfMonth());

        List<CategorySpendingProjection> projections =
                analyticsRepository.findCategorySpending(
                        user,
                        TransactionType.EXPENSE,
                        startDate,
                        endDate
                );

        BigDecimal totalSpending =
                projections.stream()
                        .map(CategorySpendingProjection::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return projections.stream()
                .map(projection -> {

                    BigDecimal percentage = BigDecimal.ZERO;

                    if (totalSpending.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = projection.getAmount()
                                .multiply(BigDecimal.valueOf(100))
                                .divide(
                                        totalSpending,
                                        2,
                                        RoundingMode.HALF_UP
                                );
                    }

                    return new CategorySpendingResponse(
                            projection.getCategory(),
                            projection.getAmount(),
                            percentage
                    );
                })
                .toList();
    }

    public HighestSpendingCategoryResponse getHighestSpendingCategory(
            String email,
            LocalDate month) {

        List<CategorySpendingResponse> spending =
                getCategorySpending(email, month);

        if (spending.isEmpty()) {
            return null;
        }

        CategorySpendingResponse highest = spending.get(0);

        return new HighestSpendingCategoryResponse(
                highest.category(),
                highest.amount(),
                highest.percentage()
        );
    }

    public MonthlyFinancialOverviewResponse getMonthlyOverview(
            String email,
            LocalDate month) {

        MonthlyFinancialSummaryResponse summary =
                getMonthlySummary(email, month);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<Budget> budgets =
                budgetRepository.findByUserAndBudgetMonth(
                        user,
                        month
                );

        List<BudgetUtilizationResponse> budgetUtilization =
                budgets.stream()
                        .map(budget ->
                                budgetService.getBudgetUtilization(
                                        budget.getId(),
                                        email
                                )
                        )
                        .toList();

        BigDecimal totalBudget =
                budgetUtilization.stream()
                        .map(BudgetUtilizationResponse::budgetAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBudgetSpent =
                budgetUtilization.stream()
                        .map(BudgetUtilizationResponse::spentAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBudgetRemaining =
                totalBudget.subtract(totalBudgetSpent);

        BigDecimal budgetUtilizationPercentage =
                BigDecimal.ZERO;

        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            budgetUtilizationPercentage =
                    totalBudgetSpent
                            .multiply(BigDecimal.valueOf(100))
                            .divide(
                                    totalBudget,
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        HighestSpendingCategoryResponse highestSpending =
                getHighestSpendingCategory(email, month);

        Category highestCategory = null;
        BigDecimal highestAmount = BigDecimal.ZERO;

        if (highestSpending != null) {
            highestCategory = highestSpending.category();
            highestAmount = highestSpending.amount();
        }

        return new MonthlyFinancialOverviewResponse(
                month,
                summary.totalIncome(),
                summary.totalExpense(),
                summary.netBalance(),
                totalBudget,
                totalBudgetSpent,
                totalBudgetRemaining,
                budgetUtilizationPercentage,
                highestCategory,
                highestAmount
        );
    }

    public MonthlyComparisonResponse getMonthlyComparison(
            String email,
            LocalDate month) {

        MonthlyFinancialSummaryResponse current =
                getMonthlySummary(email, month);

        LocalDate previousMonth = month.minusMonths(1);

        MonthlyFinancialSummaryResponse previous =
                getMonthlySummary(email, previousMonth);

        BigDecimal incomeChange =
                current.totalIncome()
                        .subtract(previous.totalIncome());

        BigDecimal expenseChange =
                current.totalExpense()
                        .subtract(previous.totalExpense());

        BigDecimal netBalanceChange =
                current.netBalance()
                        .subtract(previous.netBalance());

        return new MonthlyComparisonResponse(
                month,
                previousMonth,
                current.totalIncome(),
                previous.totalIncome(),
                current.totalExpense(),
                previous.totalExpense(),
                incomeChange,
                expenseChange,
                current.netBalance(),
                previous.netBalance(),
                netBalanceChange
        );
    }
}