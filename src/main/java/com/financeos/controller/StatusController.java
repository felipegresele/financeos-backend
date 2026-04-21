package com.financeos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class StatusController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "status", "🟢 FinanceOS API ON",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
