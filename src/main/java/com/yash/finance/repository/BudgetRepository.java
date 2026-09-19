package com.yash.finance.repository;

import com.yash.finance.entity.Budget;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserAndCategoryAndBudgetMonth(
            User user,
            Category category,
            LocalDate budgetMonth
    );

    List<Budget> findByUser(User user);

    Optional<Budget> findByIdAndUser(Long id, User user);

    List<Budget> findByUserAndBudgetMonth(
            User user,
            LocalDate budgetMonth
    );
}

