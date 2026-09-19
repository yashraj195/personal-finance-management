package com.yash.finance.repository;

import com.yash.finance.entity.Category;
import com.yash.finance.entity.Transaction;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,JpaSpecificationExecutor<Transaction> {

    List<Transaction> findByUser(User user);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    List<Transaction> findByUserAndType(
            User user,
            TransactionType type
    );

    List<Transaction> findByUserAndCategory(
            User user,
            Category category
    );

    List<Transaction> findByUserAndTransactionDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user = :user
        AND t.category = :category
        AND t.type = :type
        AND t.transactionDate >= :startDate
        AND t.transactionDate <= :endDate
        """)
    BigDecimal sumAmount(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}