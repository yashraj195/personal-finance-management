package com.yash.finance.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.yash.finance.dto.MonthlyComparisonResponse;
import com.yash.finance.dto.MonthlyFinancialOverviewResponse;
import com.yash.finance.dto.HighestSpendingCategoryResponse;
import com.yash.finance.dto.CategorySpendingResponse;
import com.yash.finance.dto.MonthlyFinancialSummaryResponse;
import com.yash.finance.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;
import java.time.LocalDate;

@Tag(
        name = "Analytics",
        description = "Financial spending and budget analytics"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Monthly summary retrieved successfully"
            )
    })
    @Operation(
            summary = "Get Monthly Summary",
            description = "Returns monthly summary of income and expense"
    )
    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlyFinancialSummaryResponse> getMonthlySummary(
            @RequestParam LocalDate month,
            Authentication authentication) {

        return ResponseEntity.ok(
                analyticsService.getMonthlySummary(
                        authentication.getName(),
                        month
                )
        );
    }

    @Operation(
            summary = "Get category spending summary",
            description = "Returns spending totals grouped by transaction category for a selected month."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category spending summary retrieved successfully"
            )
    })
    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendingResponse>> getCategorySpending(
            @RequestParam LocalDate month,
            Authentication authentication) {

        return ResponseEntity.ok(
                analyticsService.getCategorySpending(
                        authentication.getName(),
                        month
                )
        );
    }
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Highest spending category retrieved successfully"
            )
    })
    @Operation(
            summary = "Get highest spending category",
            description = "Returns the category with the highest expense amount for the selected month."
    )
    @GetMapping("/highest-spending")
    public ResponseEntity<HighestSpendingCategoryResponse> getHighestSpendingCategory(
            @RequestParam LocalDate month,
            Authentication authentication) {

        return ResponseEntity.ok(
                analyticsService.getHighestSpendingCategory(
                        authentication.getName(),
                        month
                )
        );
    }
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Monthly Overview retrieved successfully"
            )
    })
    @Operation(
            summary = "Analytics Overview",
            description = "Returns the overview of analytics for a selected user."
    )
    @GetMapping("/overview")
    public ResponseEntity<MonthlyFinancialOverviewResponse> getMonthlyOverview(
            @RequestParam LocalDate month,
            Authentication authentication) {

        return ResponseEntity.ok(
                analyticsService.getMonthlyOverview(
                        authentication.getName(),
                        month
                )
        );
    }
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Monthly spending comparison retrieved successfully"
            )
    })
    @Operation(
            summary = "Get monthly spending comparison",
            description = "Compares income, expenses and net balance for the selected month with the previous month."
    )
    @GetMapping("/monthly-comparison")
    public ResponseEntity<MonthlyComparisonResponse> getMonthlyComparison(
            @RequestParam LocalDate month,
            Authentication authentication) {

        return ResponseEntity.ok(
                analyticsService.getMonthlyComparison(
                        authentication.getName(),
                        month
                )
        );
    }
}

