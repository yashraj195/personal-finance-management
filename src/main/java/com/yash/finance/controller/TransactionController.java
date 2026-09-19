package com.yash.finance.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.yash.finance.dto.TransactionRequest;
import com.yash.finance.dto.TransactionResponse;
import com.yash.finance.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.yash.finance.entity.Category;
import com.yash.finance.entity.TransactionType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;

@Tag(
        name = "Transactions",
        description = "Manage income and expense transactions"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Create transaction",
            description = "Creates an income or expense transaction for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid transaction data"
            )
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        TransactionResponse response =
                transactionService.createTransaction(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get transactions",
            description = "Returns the authenticated user's transactions with optional filtering and pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transactions retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 10, sort = "transactionDate")
            Pageable pageable,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactions(
                        authentication.getName(),
                        type,
                        category,
                        startDate,
                        endDate,
                        pageable
                )
        );
    }

    @Operation(
            summary = "Get transaction by ID",
            description = "Returns a specific transaction belonging to the authenticated user."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransaction(
                        id,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Update transaction",
            description = "Updates a transaction belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid transaction data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Delete transaction",
            description = "Deletes a transaction belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Transaction deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        transactionService.deleteTransaction(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}