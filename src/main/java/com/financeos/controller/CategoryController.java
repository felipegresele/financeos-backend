package com.financeos.controller;

import com.financeos.dto.CategoryDTOs;
import com.financeos.entity.User;
import com.financeos.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTOs.CategoryResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryService.getAll(user));
    }

    @PostMapping
    public ResponseEntity<CategoryDTOs.CategoryResponse> create(@Valid @RequestBody CategoryDTOs.CategoryRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(categoryService.create(req, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTOs.CategoryResponse> update(@PathVariable String id, @Valid @RequestBody CategoryDTOs.CategoryRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryService.update(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal User user) {
        categoryService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
