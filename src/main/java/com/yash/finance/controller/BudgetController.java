package com.yash.finance.controller;

import java.util.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.yash.finance.dto.MonthlyBudgetOverviewResponse;
import com.yash.finance.dto.BudgetUtilizationResponse;
import com.yash.finance.dto.BudgetRequest;
import com.yash.finance.dto.BudgetResponse;
import com.yash.finance.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Tag(
        name = "Budgets",
        description = "Manage monthly spending budgets"
)
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Budget created successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid budget data"
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Budget already exists for this category and month"
        )
})
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Operation(
            summary = "Create budget",
            description = "Creates a monthly spending budget for a category for the authenticated user."
    )
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        budgetService.createBudget(
                                request,
                                authentication.getName()
                        )
                );
    }

    @Operation(
            summary = "Get budgets",
            description = "Returns all budgets belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Budgets retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.getBudgets(
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Get budget by ID",
            description = "Returns a specific budget belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Budget retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.getBudget(
                        id,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Update budget",
            description = "Updates a budget belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Budget updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid budget data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.updateBudget(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Get monthly budget overview",
            description = "Returns the combined budget and spending overview for a month."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Monthly budget overview retrieved successfully"
            )
    })
    @GetMapping("/overview")
    public ResponseEntity<MonthlyBudgetOverviewResponse> getMonthlyOverview(
            @RequestParam LocalDate month,
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.getMonthlyOverview(
                        authentication.getName(),
                        month
                )
        );
    }

    @Operation(
            summary = "Delete budget",
            description = "Deletes a budget belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Budget deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id,
            Authentication authentication) {

        budgetService.deleteBudget(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get budget utilisation",
            description = "Returns spending, remaining amount and utilisation percentage for a specific budget."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Budget utilisation retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found"
            )
    })
    @GetMapping("/{id}/utilization")
    public ResponseEntity<BudgetUtilizationResponse> getBudgetUtilization(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                budgetService.getBudgetUtilization(
                        id,
                        authentication.getName()
                )
        );
    }
}