package com.financeos.controller;

import com.financeos.dto.TransactionDTOs;
import com.financeos.entity.User;
import com.financeos.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<TransactionDTOs.PagedTransactionsResponse> list(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search
    ) {
        var filter = TransactionDTOs.TransactionFilter.builder()
                .startDate(startDate).endDate(endDate)
                .type(type != null ? com.financeos.entity.Transaction.TransactionType.valueOf(type) : null)
                .categoryId(categoryId).search(search).build();
        return ResponseEntity.ok(transactionService.getTransactions(user, filter, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTOs.TransactionResponse> getById(@PathVariable String id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<TransactionDTOs.TransactionResponse> create(@Valid @RequestBody TransactionDTOs.TransactionRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(transactionService.create(req, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTOs.TransactionResponse> update(@PathVariable String id, @Valid @RequestBody TransactionDTOs.TransactionRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.update(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal User user) {
        transactionService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}

