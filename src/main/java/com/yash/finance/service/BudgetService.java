package com.yash.finance.service;

import com.yash.finance.dto.BudgetRequest;
import com.yash.finance.dto.BudgetResponse;
import com.yash.finance.dto.BudgetUtilizationResponse;
import com.yash.finance.dto.MonthlyBudgetOverviewResponse;
import com.yash.finance.entity.Budget;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.entity.User;
import com.yash.finance.exception.DuplicateResourceException;
import com.yash.finance.exception.ResourceNotFoundException;
import com.yash.finance.repository.BudgetRepository;
import com.yash.finance.repository.TransactionRepository;
import com.yash.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {

        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public BudgetResponse createBudget(
            BudgetRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (budgetRepository
                .findByUserAndCategoryAndBudgetMonth(
                        user,
                        request.category(),
                        request.budgetMonth()
                )
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Budget already exists for this category and month"
            );
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(request.category())
                .amount(request.amount())
                .budgetMonth(request.budgetMonth())
                .build();

        Budget savedBudget = budgetRepository.save(budget);

        return toResponse(savedBudget);
    }

    private BudgetResponse toResponse(Budget budget) {

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory(),
                budget.getAmount(),
                budget.getBudgetMonth()
        );
    }

    public List<BudgetResponse> getBudgets(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return budgetRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BudgetResponse getBudget(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Budget budget = budgetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

        return toResponse(budget);
    }

    public BudgetResponse updateBudget(
            Long id,
            BudgetRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Budget budget = budgetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

        budget.setCategory(request.category());
        budget.setAmount(request.amount());
        budget.setBudgetMonth(request.budgetMonth());

        Budget updatedBudget = budgetRepository.save(budget);

        return toResponse(updatedBudget);
    }

    public void deleteBudget(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Budget budget = budgetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

        budgetRepository.delete(budget);
    }

    public BudgetUtilizationResponse getBudgetUtilization(
            Long id,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Budget budget = budgetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));

        LocalDate startDate = budget.getBudgetMonth();

        LocalDate endDate = startDate
                .withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal spentAmount =
                transactionRepository.sumAmount(
                        user,
                        budget.getCategory(),
                        TransactionType.EXPENSE,
                        startDate,
                        endDate
                );

        BigDecimal budgetAmount = budget.getAmount();

        BigDecimal remainingAmount =
                budgetAmount.subtract(spentAmount);

        BigDecimal utilizationPercentage =
                spentAmount
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                budgetAmount,
                                2,
                                RoundingMode.HALF_UP
                        );

        String status;

        if (spentAmount.compareTo(budgetAmount) > 0) {
            status = "EXCEEDED";
        } else if (spentAmount.compareTo(
                budgetAmount.multiply(
                        BigDecimal.valueOf(0.8)
                )) >= 0) {

            status = "APPROACHING_LIMIT";
        } else {
            status = "WITHIN_LIMIT";
        }

        return new BudgetUtilizationResponse(
                budget.getId(),
                budget.getCategory(),
                budgetAmount,
                spentAmount,
                remainingAmount,
                utilizationPercentage,
                status
        );
    }

    public MonthlyBudgetOverviewResponse getMonthlyOverview(
            String email,
            LocalDate month) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<Budget> budgets =
                budgetRepository.findByUserAndBudgetMonth(
                        user,
                        month
                );

        List<BudgetUtilizationResponse> utilization =
                budgets.stream()
                        .map(budget ->
                                getBudgetUtilization(
                                        budget.getId(),
                                        email
                                )
                        )
                        .toList();

        BigDecimal totalBudget =
                utilization.stream()
                        .map(BudgetUtilizationResponse::budgetAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent =
                utilization.stream()
                        .map(BudgetUtilizationResponse::spentAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRemaining =
                totalBudget.subtract(totalSpent);

        BigDecimal overallUtilization =
                BigDecimal.ZERO;

        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            overallUtilization =
                    totalSpent
                            .multiply(BigDecimal.valueOf(100))
                            .divide(
                                    totalBudget,
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        return new MonthlyBudgetOverviewResponse(
                month,
                totalBudget,
                totalSpent,
                totalRemaining,
                overallUtilization,
                utilization
        );
    }
}