package com.financeos.dto;

import com.financeos.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class CategoryDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String color;
        @NotBlank
        private String icon;
        @NotNull
        private Category.CategoryType type;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryResponse {
        private String id;
        private String name;
        private String color;
        private String icon;
        private Category.CategoryType type;
        private boolean isDefault;
        private long transactionCount;
    }
}
