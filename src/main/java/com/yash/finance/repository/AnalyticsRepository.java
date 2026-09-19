package com.yash.finance.repository;

import com.yash.finance.entity.Transaction;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface AnalyticsRepository extends Repository<Transaction, Long> {

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user = :user
            AND t.type = :type
            AND t.transactionDate >= :startDate
            AND t.transactionDate <= :endDate
            """)
    BigDecimal sumByTypeAndDateRange(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT t.category AS category,
               COALESCE(SUM(t.amount), 0) AS amount
        FROM Transaction t
        WHERE t.user = :user
        AND t.type = :type
        AND t.transactionDate >= :startDate
        AND t.transactionDate <= :endDate
        GROUP BY t.category
        ORDER BY SUM(t.amount) DESC
        """)
    List<CategorySpendingProjection> findCategorySpending(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}