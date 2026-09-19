package com.yash.finance.service;

import com.yash.finance.exception.ResourceNotFoundException;
import com.yash.finance.dto.TransactionRequest;
import com.yash.finance.dto.TransactionResponse;
import com.yash.finance.entity.Transaction;
import com.yash.finance.entity.User;
import com.yash.finance.repository.TransactionRepository;
import com.yash.finance.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.specification.TransactionSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public TransactionResponse createTransaction(
            TransactionRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(request.type())
                .category(request.category())
                .amount(request.amount())
                .transactionDate(request.transactionDate())
                .description(request.description())
                .build();

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return toResponse(savedTransaction);
    }

    private TransactionResponse toResponse(Transaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription()
        );
    }

    public Page<TransactionResponse> getTransactions(
            String email,
            TransactionType type,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Specification<Transaction> specification =
                Specification.where(
                        TransactionSpecification.hasUser(user)
                );

        if (type != null) {
            specification = specification.and(
                    TransactionSpecification.hasType(type)
            );
        }

        if (category != null) {
            specification = specification.and(
                    TransactionSpecification.hasCategory(category)
            );
        }

        if (startDate != null) {
            specification = specification.and(
                    TransactionSpecification.dateAfterOrEqual(startDate)
            );
        }

        if (endDate != null) {
            specification = specification.and(
                    TransactionSpecification.dateBeforeOrEqual(endDate)
            );
        }

        return transactionRepository.findAll(specification, pageable)
                .map(this::toResponse);
    }

    public TransactionResponse getTransaction(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                         new ResourceNotFoundException("Transaction not found"));

        return toResponse(transaction);
    }

    public TransactionResponse updateTransaction(
            Long id,
            TransactionRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found"));

        transaction.setType(request.type());
        transaction.setCategory(request.category());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());

        Transaction updatedTransaction =
                transactionRepository.save(transaction);

        return toResponse(updatedTransaction);
    }

    public void deleteTransaction(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);
    }
}