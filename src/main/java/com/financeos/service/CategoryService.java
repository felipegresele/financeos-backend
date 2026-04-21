package com.financeos.service;

import com.financeos.dto.CategoryDTOs.*;
import com.financeos.entity.Category;
import com.financeos.entity.User;
import com.financeos.exception.BusinessException;
import com.financeos.exception.ResourceNotFoundException;
import com.financeos.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(User user) {
        return categoryRepository.findByUserIdOrIsDefaultTrue(user.getId())
            .stream().map(this::toResponse).toList();
    }

    public CategoryResponse create(CategoryRequest req, User user) {
        if (categoryRepository.existsByNameAndUserId(req.getName(), user.getId())) {
            throw new BusinessException("Category name already exists");
        }
        var cat = Category.builder()
            .name(req.getName())
            .color(req.getColor())
            .icon(req.getIcon())
            .type(req.getType())
            .user(user)
            .build();
        return toResponse(categoryRepository.save(cat));
    }

    public CategoryResponse update(String id, CategoryRequest req, User user) {
        var cat = categoryRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (cat.isDefault()) throw new BusinessException("Cannot edit default categories");
        cat.setName(req.getName());
        cat.setColor(req.getColor());
        cat.setIcon(req.getIcon());
        cat.setType(req.getType());
        return toResponse(categoryRepository.save(cat));
    }

    public void delete(String id, User user) {
        var cat = categoryRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (cat.isDefault()) throw new BusinessException("Cannot delete default categories");
        if (!cat.getTransactions().isEmpty()) throw new BusinessException("Category has transactions");
        categoryRepository.delete(cat);
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
            .id(c.getId()).name(c.getName())
            .color(c.getColor()).icon(c.getIcon())
            .type(c.getType()).isDefault(c.isDefault())
            .transactionCount(c.getTransactions().size())
            .build();
    }
}
