package com.financeos.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal balance;
        private BigDecimal savingsRate;
        private List<MonthlyData> monthlyData;
        private List<CategoryBreakdown> expensesByCategory;
        private List<CategoryBreakdown> incomeByCategory;
        private List<TransactionDTOs.TransactionResponse> recentTransactions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyData {
        private String month;
        private BigDecimal income;
        private BigDecimal expenses;
        private BigDecimal balance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryBreakdown {
        private String categoryId;
        private String categoryName;
        private String color;
        private String icon;
        private BigDecimal total;
        private double percentage;
    }
}
