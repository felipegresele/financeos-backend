package com.financeos.dto;

import com.financeos.entity.Goal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalRequest {
        @NotBlank
        private String name;
        @NotNull
        @DecimalMin("0.01")
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private String icon;
        private String color;
        private LocalDate deadline;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalDepositRequest {
        @NotNull
        @DecimalMin("0.01")
        private BigDecimal amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoalResponse {
        private String id;
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private String icon;
        private String color;
        private LocalDate deadline;
        private Goal.GoalStatus status;
        private double progressPercent;
        private BigDecimal remaining;
    }
}
