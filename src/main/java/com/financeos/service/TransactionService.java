package com.financeos.service;

import com.financeos.dto.TransactionDTOs.*;
import com.financeos.dto.CategoryDTOs.CategoryResponse;
import com.financeos.entity.Transaction;
import com.financeos.entity.User;
import com.financeos.exception.BusinessException;
import com.financeos.exception.ResourceNotFoundException;
import com.financeos.repository.CategoryRepository;
import com.financeos.repository.TransactionRepository;
import com.financeos.websocket.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public PagedTransactionsResponse getTransactions(User user, TransactionFilter filter, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Transaction> result = transactionRepository.findWithFilters(
            user.getId(),
            filter.getStartDate(),
            filter.getEndDate(),
            filter.getType(),
            filter.getCategoryId(),
            filter.getSearch(),
            pageable
        );
        return PagedTransactionsResponse.builder()
            .content(result.getContent().stream().map(this::toResponse).toList())
            .page(result.getNumber())
            .size(result.getSize())
            .totalElements(result.getTotalElements())
            .totalPages(result.getTotalPages())
            .build();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(String id, User user) {
        return toResponse(findOwned(id, user));
    }

    public TransactionResponse create(TransactionRequest req, User user) {
        var transaction = Transaction.builder()
            .description(req.getDescription())
            .amount(req.getAmount())
            .type(req.getType())
            .date(req.getDate())
            .notes(req.getNotes())
            .recurring(req.isRecurring())
            .user(user)
            .build();

        if (req.getCategoryId() != null) {
            var category = categoryRepository.findByIdAndUserId(req.getCategoryId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            transaction.setCategory(category);
        }

        transaction = transactionRepository.save(transaction);
        notificationService.notifyUser(user.getId(), "transaction_created", toResponse(transaction));
        return toResponse(transaction);
    }

    public TransactionResponse update(String id, TransactionRequest req, User user) {
        var transaction = findOwned(id, user);
        transaction.setDescription(req.getDescription());
        transaction.setAmount(req.getAmount());
        transaction.setType(req.getType());
        transaction.setDate(req.getDate());
        transaction.setNotes(req.getNotes());
        transaction.setRecurring(req.isRecurring());

        if (req.getCategoryId() != null) {
            var category = categoryRepository.findByIdAndUserId(req.getCategoryId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            transaction.setCategory(category);
        } else {
            transaction.setCategory(null);
        }
        return toResponse(transactionRepository.save(transaction));
    }

    public void delete(String id, User user) {
        var transaction = findOwned(id, user);
        transactionRepository.delete(transaction);
        notificationService.notifyUser(user.getId(), "transaction_deleted", id);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentTransactions(User user) {
        return transactionRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId())
            .stream().map(this::toResponse).toList();
    }

    private Transaction findOwned(String id, User user) {
        var t = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!t.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Access denied");
        }
        return t;
    }

    public TransactionResponse toResponse(Transaction t) {
        CategoryResponse cat = null;
        if (t.getCategory() != null) {
            var c = t.getCategory();
            cat = CategoryResponse.builder()
                .id(c.getId()).name(c.getName())
                .color(c.getColor()).icon(c.getIcon())
                .type(c.getType()).isDefault(c.isDefault()).build();
        }
        return TransactionResponse.builder()
            .id(t.getId())
            .description(t.getDescription())
            .amount(t.getAmount())
            .type(t.getType())
            .date(t.getDate())
            .notes(t.getNotes())
            .recurring(t.isRecurring())
            .category(cat)
            .build();
    }
}
