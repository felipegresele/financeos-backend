package com.financeos.dto;

import com.financeos.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionRequest {
        @NotBlank
        private String description;

        @NotNull
        @DecimalMin("0.01")
        private BigDecimal amount;

        @NotNull
        private Transaction.TransactionType type;
        @NotNull
        private LocalDate date;

        private String categoryId;
        private String notes;
        private boolean recurring;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionResponse {
        private String id;
        private String description;
        private BigDecimal amount;
        private Transaction.TransactionType type;
        private LocalDate date;
        private String notes;
        private boolean recurring;
        private CategoryDTOs.CategoryResponse category;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionFilter {
        private LocalDate startDate;
        private LocalDate endDate;
        private Transaction.TransactionType type;
        private String categoryId;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
        private String search;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PagedTransactionsResponse {
        private List<TransactionResponse> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
