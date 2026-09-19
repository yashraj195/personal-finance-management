package com.yash.finance.service;

import com.yash.finance.exception.ResourceNotFoundException;
import com.yash.finance.dto.TransactionRequest;
import com.yash.finance.dto.TransactionResponse;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.Role;
import com.yash.finance.entity.Transaction;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.entity.User;
import com.yash.finance.repository.TransactionRepository;
import com.yash.finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transactionService = new TransactionService(
                transactionRepository,
                userRepository
        );
    }

    @Test
    void shouldCreateTransactionSuccessfully() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        TransactionRequest request = new TransactionRequest(
                TransactionType.EXPENSE,
                Category.FOOD,
                new BigDecimal("500.00"),
                LocalDate.of(2026, 9, 19),
                "Lunch"
        );

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .user(user)
                .type(request.type())
                .category(request.category())
                .amount(request.amount())
                .transactionDate(request.transactionDate())
                .description(request.description())
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        // Act
        TransactionResponse response =
                transactionService.createTransaction(request, email);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(TransactionType.EXPENSE, response.type());
        assertEquals(Category.FOOD, response.category());
        assertEquals(new BigDecimal("500.00"), response.amount());
        assertEquals(LocalDate.of(2026, 9, 19), response.transactionDate());
        assertEquals("Lunch", response.description());

        verify(userRepository).findByEmail(email);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        String email = "unknown@example.com";

        TransactionRequest request = new TransactionRequest(
                TransactionType.EXPENSE,
                Category.FOOD,
                new BigDecimal("500.00"),
                LocalDate.of(2026, 9, 19),
                "Lunch"
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionService.createTransaction(request, email)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldThrowExceptionWhenTransactionDoesNotBelongToUser() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByIdAndUser(999L, user))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService.getTransaction(999L, email)
        );

        assertEquals("Transaction not found", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verify(transactionRepository).findByIdAndUser(999L, user);
    }

    @Test
    void shouldUpdateTransactionSuccessfully() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        Transaction existingTransaction = Transaction.builder()
                .id(1L)
                .user(user)
                .type(TransactionType.EXPENSE)
                .category(Category.FOOD)
                .amount(new BigDecimal("500.00"))
                .transactionDate(LocalDate.of(2026, 9, 19))
                .description("Lunch")
                .build();

        TransactionRequest updateRequest = new TransactionRequest(
                TransactionType.EXPENSE,
                Category.TRANSPORT,
                new BigDecimal("750.00"),
                LocalDate.of(2026, 9, 20),
                "Cab"
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(existingTransaction));

        when(transactionRepository.save(existingTransaction))
                .thenReturn(existingTransaction);

        // Act
        TransactionResponse response =
                transactionService.updateTransaction(
                        1L,
                        updateRequest,
                        email
                );

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(TransactionType.EXPENSE, response.type());
        assertEquals(Category.TRANSPORT, response.category());
        assertEquals(new BigDecimal("750.00"), response.amount());
        assertEquals(
                LocalDate.of(2026, 9, 20),
                response.transactionDate()
        );
        assertEquals("Cab", response.description());

        verify(userRepository).findByEmail(email);
        verify(transactionRepository).findByIdAndUser(1L, user);
        verify(transactionRepository).save(existingTransaction);
    }

    @Test
    void shouldDeleteTransactionSuccessfully() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(user)
                .type(TransactionType.EXPENSE)
                .category(Category.FOOD)
                .amount(new BigDecimal("500.00"))
                .transactionDate(LocalDate.of(2026, 9, 19))
                .description("Lunch")
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(transaction));

        // Act
        transactionService.deleteTransaction(1L, email);

        // Assert
        verify(userRepository).findByEmail(email);
        verify(transactionRepository).findByIdAndUser(1L, user);
        verify(transactionRepository).delete(transaction);
    }
}

