package com.financeos.controller;

import com.financeos.dto.GoalDTOs;
import com.financeos.entity.User;
import com.financeos.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
class GoalController {
    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalDTOs.GoalResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getAll(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalDTOs.GoalResponse> getById(@PathVariable String id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<GoalDTOs.GoalResponse> create(@Valid @RequestBody GoalDTOs.GoalRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(goalService.create(req, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalDTOs.GoalResponse> update(@PathVariable String id, @Valid @RequestBody GoalDTOs.GoalRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.update(id, req, user));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<GoalDTOs.GoalResponse> deposit(@PathVariable String id, @Valid @RequestBody GoalDTOs.GoalDepositRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.deposit(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal User user) {
        goalService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
