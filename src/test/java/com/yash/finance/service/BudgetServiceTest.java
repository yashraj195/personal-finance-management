package com.yash.finance.service;

import com.yash.finance.dto.BudgetRequest;
import com.yash.finance.dto.BudgetResponse;
import com.yash.finance.entity.Budget;
import com.yash.finance.entity.TransactionType;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.Role;
import com.yash.finance.entity.User;
import com.yash.finance.repository.BudgetRepository;
import com.yash.finance.repository.TransactionRepository;
import com.yash.finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.yash.finance.exception.DuplicateResourceException;
import com.yash.finance.dto.BudgetUtilizationResponse;
import com.yash.finance.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        budgetService = new BudgetService(
                budgetRepository,
                userRepository,
                transactionRepository
        );
    }

    @Test
    void shouldCreateBudgetSuccessfully() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        BudgetRequest request = new BudgetRequest(
                Category.FOOD,
                new BigDecimal("10000.00"),
                LocalDate.of(2026, 9, 1)
        );

        Budget savedBudget = Budget.builder()
                .id(1L)
                .user(user)
                .category(request.category())
                .amount(request.amount())
                .budgetMonth(request.budgetMonth())
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(budgetRepository
                .findByUserAndCategoryAndBudgetMonth(
                        user,
                        request.category(),
                        request.budgetMonth()
                ))
                .thenReturn(Optional.empty());

        when(budgetRepository.save(any(Budget.class)))
                .thenReturn(savedBudget);

        // Act
        BudgetResponse response =
                budgetService.createBudget(request, email);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(Category.FOOD, response.category());
        assertEquals(
                new BigDecimal("10000.00"),
                response.amount()
        );
        assertEquals(
                LocalDate.of(2026, 9, 1),
                response.budgetMonth()
        );

        verify(userRepository).findByEmail(email);

        verify(budgetRepository)
                .findByUserAndCategoryAndBudgetMonth(
                        user,
                        request.category(),
                        request.budgetMonth()
                );

        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void shouldThrowExceptionWhenBudgetAlreadyExists() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        BudgetRequest request = new BudgetRequest(
                Category.FOOD,
                new BigDecimal("10000.00"),
                LocalDate.of(2026, 9, 1)
        );

        Budget existingBudget = Budget.builder()
                .id(1L)
                .user(user)
                .category(Category.FOOD)
                .amount(new BigDecimal("10000.00"))
                .budgetMonth(LocalDate.of(2026, 9, 1))
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(budgetRepository
                .findByUserAndCategoryAndBudgetMonth(
                        user,
                        request.category(),
                        request.budgetMonth()
                ))
                .thenReturn(Optional.of(existingBudget));

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> budgetService.createBudget(request, email)
        );

        assertEquals(
                "Budget already exists for this category and month",
                exception.getMessage()
        );

        verify(userRepository).findByEmail(email);

        verify(budgetRepository)
                .findByUserAndCategoryAndBudgetMonth(
                        user,
                        request.category(),
                        request.budgetMonth()
                );

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void shouldCalculateBudgetUtilizationCorrectly() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        Budget budget = Budget.builder()
                .id(1L)
                .user(user)
                .category(Category.FOOD)
                .amount(new BigDecimal("10000.00"))
                .budgetMonth(LocalDate.of(2026, 9, 1))
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(budgetRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(budget));

        when(transactionRepository.sumAmount(
                user,
                Category.FOOD,
                TransactionType.EXPENSE,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        )).thenReturn(new BigDecimal("5000.00"));

        // Act
        BudgetUtilizationResponse response =
                budgetService.getBudgetUtilization(1L, email);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.budgetId());
        assertEquals(Category.FOOD, response.category());
        assertEquals(new BigDecimal("10000.00"), response.budgetAmount());
        assertEquals(new BigDecimal("5000.00"), response.spentAmount());
        assertEquals(new BigDecimal("5000.00"), response.remainingAmount());
        assertEquals(new BigDecimal("50.00"), response.utilizationPercentage());
        assertEquals("WITHIN_LIMIT", response.status());

        verify(userRepository).findByEmail(email);
        verify(budgetRepository).findByIdAndUser(1L, user);

        verify(transactionRepository).sumAmount(
                user,
                Category.FOOD,
                TransactionType.EXPENSE,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );
    }

    @Test
    void shouldMarkBudgetAsApproachingLimit() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        Budget budget = Budget.builder()
                .id(1L)
                .user(user)
                .category(Category.FOOD)
                .amount(new BigDecimal("10000.00"))
                .budgetMonth(LocalDate.of(2026, 9, 1))
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(budgetRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(budget));

        when(transactionRepository.sumAmount(
                user,
                Category.FOOD,
                TransactionType.EXPENSE,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        )).thenReturn(new BigDecimal("8000.00"));

        // Act
        BudgetUtilizationResponse response =
                budgetService.getBudgetUtilization(1L, email);

        // Assert
        assertEquals(new BigDecimal("8000.00"), response.spentAmount());
        assertEquals(new BigDecimal("2000.00"), response.remainingAmount());
        assertEquals(new BigDecimal("80.00"), response.utilizationPercentage());
        assertEquals("APPROACHING_LIMIT", response.status());

        verify(transactionRepository).sumAmount(
                user,
                Category.FOOD,
                TransactionType.EXPENSE,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );
    }

    @Test
    void shouldMarkBudgetAsExceeded() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        Budget budget = Budget.builder()
                .id(1L)
                .user(user)
                .category(Category.FOOD)
                .amount(new BigDecimal("10000.00"))
                .budgetMonth(LocalDate.of(2026, 9, 1))
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(budgetRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(budget));

        when(transactionRepository.sumAmount(
                user,
                Category.FOOD,
                TransactionType.EXPENSE,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        )).thenReturn(new BigDecimal("12000.00"));

        // Act
        BudgetUtilizationResponse response =
                budgetService.getBudgetUtilization(1L, email);

        // Assert
        assertEquals(new BigDecimal("12000.00"), response.spentAmount());
        assertEquals(new BigDecimal("-2000.00"), response.remainingAmount());
        assertEquals(new BigDecimal("120.00"), response.utilizationPercentage());
        assertEquals("EXCEEDED", response.status());

        verify(transactionRepository).sumAmount(
                user,
                Category.FOOD,
                TransactionType.EXPENSE,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30)
        );
    }

    @Test
    void shouldUpdateBudgetSuccessfully() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        Budget existingBudget = Budget.builder()
                .id(1L)
                .user(user)
                .category(Category.FOOD)
                .amount(new BigDecimal("10000.00"))
                .budgetMonth(LocalDate.of(2026, 9, 1))
                .build();

        BudgetRequest updateRequest = new BudgetRequest(
                Category.TRANSPORT,
                new BigDecimal("15000.00"),
                LocalDate.of(2026, 10, 1)
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(budgetRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(existingBudget));

        when(budgetRepository.save(existingBudget))
                .thenReturn(existingBudget);

        // Act
        BudgetResponse response =
                budgetService.updateBudget(
                        1L,
                        updateRequest,
                        email
                );

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(Category.TRANSPORT, response.category());
        assertEquals(
                new BigDecimal("15000.00"),
                response.amount()
        );
        assertEquals(
                LocalDate.of(2026, 10, 1),
                response.budgetMonth()
        );

        verify(userRepository).findByEmail(email);
        verify(budgetRepository).findByIdAndUser(1L, user);
        verify(budgetRepository).save(existingBudget);
    }

    @Test
    void shouldDeleteBudgetSuccessfully() {

        // Arrange
        String email = "test@example.com";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        Budget budget = Budget.builder()
                .id(1L)
                .user(user)
                .category(Category.FOOD)
                .amount(new BigDecimal("10000.00"))
                .budgetMonth(LocalDate.of(2026, 9, 1))
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(budgetRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(budget));

        // Act
        budgetService.deleteBudget(1L, email);

        // Assert
        verify(userRepository).findByEmail(email);
        verify(budgetRepository).findByIdAndUser(1L, user);
        verify(budgetRepository).delete(budget);
    }

    @Test
    void shouldThrowExceptionWhenBudgetNotFound() {

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

        when(budgetRepository.findByIdAndUser(999L, user))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> budgetService.getBudget(999L, email)
        );

        assertEquals("Budget not found", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verify(budgetRepository).findByIdAndUser(999L, user);
    }
}