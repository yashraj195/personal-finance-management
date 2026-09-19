package com.yash.finance.specification;

import com.yash.finance.entity.Category;
import com.yash.finance.entity.Transaction;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TransactionSpecification {

    public static Specification<Transaction> hasUser(User user) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Transaction> hasType(
            TransactionType type) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Transaction> hasCategory(
            Category category) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Transaction> dateAfterOrEqual(
            LocalDate date) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("transactionDate"),
                        date
                );
    }

    public static Specification<Transaction> dateBeforeOrEqual(
            LocalDate date) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("transactionDate"),
                        date
                );
    }
}